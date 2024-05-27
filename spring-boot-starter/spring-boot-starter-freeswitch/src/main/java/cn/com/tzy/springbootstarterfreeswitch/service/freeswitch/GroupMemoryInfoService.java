package cn.com.tzy.springbootstarterfreeswitch.service.freeswitch;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.AgentStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process.ProcessNextHandler;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.CauseEnums;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.ProcessEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2

public abstract class GroupMemoryInfoService {

    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private ProcessNextHandler processNextHandler;
    /**
     * 排队电话
     */
    @Getter
    private volatile Map<String, PriorityQueue<CallQueue>> callInfoMap = new ConcurrentHashMap<>();
    /**
     * 空闲坐席 <技能组id, 空闲坐席>
     */
    private Map<String, PriorityQueue<AgentQueue>> agentInfoMap = new ConcurrentHashMap<>();
    /**
     * 通话中坐席
     */
    private Map<String, List<String>> callAgents = new ConcurrentHashMap<>();

    /**
     * 查询分组中 坐席与客户记忆
     */
   public abstract GroupMemoryInfo find(String groupId,String phone);

    /**
     * 获取有效的排队策略
     *
     * @param groupInfo
     * @return
     */
    public GroupOverFlowInfo getEffectiveOverflow(GroupInfo groupInfo) {
        if (CollectionUtils.isEmpty(groupInfo.getGroupOverflows())) {
            return null;
        }
        for (GroupOverFlowInfo groupOverflowPo : groupInfo.getGroupOverflows()) {
            PriorityQueue<CallQueue> callQueues = callInfoMap.get(groupInfo.getId());
            if (groupOverflowPo.isAvailable(callQueues == null ? 0 : callQueues.size(), groupInfo.getMaxWaitTime(), groupInfo.getCallInAnswer(), groupInfo.getCallInTotal())) {
                return groupOverflowPo;
            }
            return null;
        }
        return null;
    }

    /**
     * 获取空闲坐席
     *
     * @param groupId
     * @return
     */
    private AgentVoInfo getAgentQueue(String groupId) {
        if (CollectionUtils.isEmpty(agentInfoMap.get(groupId))) {
            return null;
        }
        AgentQueue agentQueue = agentInfoMap.get(groupId).poll();
        if (agentQueue == null) {
            return null;
        }
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentQueue.getAgentKey());
        if (agentVoInfo == null || agentVoInfo.getAgentState() != AgentStateEnum.READY) {
            return null;
        }
        return agentVoInfo;
    }

    /**
     * 坐席忙碌
     *
     * @param agentVoInfo
     */
    public void agentNotReady(AgentVoInfo agentVoInfo) {
        agentVoInfo.getGroupIds().forEach(groupId -> {
            PriorityQueue<AgentQueue> agentQueues = agentInfoMap.get(groupId);
            if (agentQueues == null) {
                return;
            }
            log.info("agent:{} not ready for group:{}", agentVoInfo.getAgentKey(), groupId);
            agentQueues.remove(new AgentQueue(1L, agentVoInfo.getAgentKey()));
            agentInfoMap.put(groupId, agentQueues);
            if (agentVoInfo.getAgentState() == AgentStateEnum.TALKING) {
                List<String> callAgentList = callAgents.get(groupId);
                if (CollectionUtils.isEmpty(callAgentList)) {
                    callAgentList = new ArrayList<>();
                }
                callAgentList.add(agentVoInfo.getAgentKey());
            }
            if (agentVoInfo.getAgentState() == AgentStateEnum.AFTER) {
                List<String> callAgentList = callAgents.get(groupId);
                if (!CollectionUtils.isEmpty(callAgentList)) {
                    callAgentList.remove(agentVoInfo.getAgentKey());
                }
            }
        });
    }

    /**
     * 坐席空闲
     *
     * @param agentVoInfo
     */
    public void agentFree(AgentVoInfo agentVoInfo) {
        if (CollectionUtils.isEmpty(agentVoInfo.getGroupIds())) {
            return;
        }
        agentVoInfo.getGroupIds().forEach(groupId -> {
            PriorityQueue<AgentQueue> agentQueues = agentInfoMap.get(groupId);
            if (agentQueues == null) {
                agentQueues = new PriorityQueue<AgentQueue>();
            }
            log.info("agent:{} ready for group:{}", agentVoInfo.getAgentKey(), groupId);
            //根据空闲策略
            GroupInfo groupInfo = RedisService.getGroupInfoManager().get(groupId);
            if (groupInfo == null) {
                return;
            }
            //坐席空闲策略接口
            AgentStrategy agentStrategy = groupInfo.getGroupAgentStrategyPo().getAgentStrategy();
            Long priority = agentStrategy.calculateLevel(agentVoInfo);
            agentQueues.offer(new AgentQueue(priority, agentVoInfo.getAgentKey()));
            agentInfoMap.put(groupId, agentQueues);

        });
    }

    /**
     * 技能组中所有空闲坐席
     *
     * @param groupId
     * @return
     */
    public List<String> getFreeAgents(String groupId) {
        PriorityQueue<AgentQueue> agentQueues = agentInfoMap.get(groupId);
        if (CollectionUtils.isEmpty(agentQueues)) {
            return null;
        }
        List<String> freeAgents = new ArrayList<>();
        for (AgentQueue agentQueue : agentQueues) {
            freeAgents.add(agentQueue.getAgentKey());
        }
        return freeAgents;
    }


    /**
     *  定时运行策略
     *  进入到队列的电话，需要定时找空闲坐席
     */
    public void execute(){
        List<GroupInfo> groupInfos = new ArrayList<>();
        for (String id : callInfoMap.keySet()) {
            GroupInfo groupInfo = RedisService.getGroupInfoManager().get(id);
            if(groupInfo != null){
                groupInfos.add(groupInfo);
            }
        }
        //排序
        groupInfos.sort((o1, o2) -> o1.getLevelValue().equals(o2.getLevelValue()) ?
                o2.getLastServiceTime().compareTo(o1.getLastServiceTime()) :
                o1.getLevelValue().compareTo(o2.getLevelValue()));
        Date date = new Date();
        for (GroupInfo groupInfo : groupInfos) {
            PriorityQueue<CallQueue> entry = callInfoMap.get(groupInfo.getId());
            if (CollectionUtils.isEmpty(entry)) {
                continue;
            }
            Iterator<CallQueue> iterator = entry.iterator();
            while (iterator.hasNext()) {
                CallQueue callQueue = iterator.next();
                //清除排队差事分组
                if (DateUtil.between(callQueue.getStartTime(),date, DateUnit.SECOND) >= callQueue.getGroupOverflowInfo().getQueueTimeout().longValue()) {
                    dynamicTask.startDelay("REMOVE_CALL_QUEUE:"+callQueue.getCallId(),0,()->{
                        queueTimeout(callQueue,date);
                    });
                    iterator.remove();
                    continue;
                }
                //查找空闲坐席
                AgentVoInfo agentVoInfo = this.getAgentQueue(groupInfo.getId());
                if (agentVoInfo == null) {
                    if (!callQueue.isPlay()) {
                        CallInfo callInfo = RedisService.getCallInfoManager().get(callQueue.getCallId());
                        processNextHandler.getStrategyHandler().handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).playPath("queue.wav").build());
                        callQueue.setPlay(true);
                    }
                    continue;
                }
                iterator.remove();
                //开始处理
                CallInfo callInfo = RedisService.getCallInfoManager().get(callQueue.getCallId());
                if (callInfo == null) {
                    continue;
                }
                dynamicTask.startDelay("handler_call_queue:"+callQueue.getCallId(),0,()->{
                    if (callQueue.isPlay()) {
                        processNextHandler.getStrategyHandler().handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).playPath("queue.wav").build());
                    }
                    callInfo.setQueueEndTime(date);
                    processNextHandler.next(ProcessEnum.AGENT_PROCESS,agentVoInfo.getAgentKey(), callQueue.getDeviceId(),callInfo);
                    agentNotReady(agentVoInfo);
                });
                //记录最后服务时间
                groupInfo.setLastServiceTime(date);
            }
        }
    }

    /**
     * 超时出队列时处理
     */
    private void queueTimeout(CallQueue callQueue,Date date){
        CallInfo callInfo = RedisService.getCallInfoManager().get(callQueue.getCallId());
        if (callInfo == null) {
            return;
        }
        callInfo.setQueueEndTime(date);
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(callQueue.getDeviceId());
        GroupOverFlowInfo groupOverflowInfo = callQueue.getGroupOverflowInfo();
        log.info("callId:{} queueTimeout, busyTimeoutType:{}", callQueue.getCallId(), groupOverflowInfo.getBusyTimeoutType());
        NextCommand nextCommand = null;
        switch (groupOverflowInfo.getBusyTimeoutType()) {
            case 1:
                //排队超时走溢出策略,1:group,2:ivr,3:vdn
                switch (groupOverflowInfo.getOverflowType()) {
                    case 1:
                        nextCommand = new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_QUEUE_OVERFLOW_GROUP, groupOverflowInfo.getOverflowValue());
                        break;
                    case 2:
                        nextCommand = new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_QUEUE_OVERFLOW_IVR, groupOverflowInfo.getOverflowValue());
                        break;
                    case 3:
                        nextCommand = new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_QUEUE_OVERFLOW_VDN, groupOverflowInfo.getOverflowValue());
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                //排队超时挂机
                callInfo.setHangupDir(3);
                callInfo.setHangupCode(CauseEnums.QUEUE_TIMEOUT.getHuangupCode());
                nextCommand = new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_HANGUP, groupOverflowInfo.getOverflowValue());
                break;
            default:
                log.warn("============:{}", callQueue);
                break;
        }
        callInfo.setQueueEndTime(date);
        if (!CollectionUtils.isEmpty(callInfo.getCallDetails())) {
            CallDetail callDetail = callInfo.getCallDetails().get(callInfo.getCallDetails().size() - 1);
            if (callDetail != null) {
                callDetail.setEndTime(date);
            }
        }
        processNextHandler.getStrategyHandler().handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).isDown(true).build());
        processNextHandler.doNextCommand(callInfo,deviceInfo,nextCommand);
    }

}
