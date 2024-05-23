package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.PlayBackCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.common.fs.Constant;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.CauseEnums;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import cn.com.tzy.springbootstarterfreeswitch.pool.AgentStrategy;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  进技能组
 */
@Log4j2
@Component
public class GroupProcessHandler {


    /**
     * 排队电话
     */
    private Map<String, PriorityQueue<CallQueue>> callInfoMap = new ConcurrentHashMap<>();
    /**
     * 空闲坐席 <技能组id, 空闲坐席>
     */
    private Map<String, PriorityQueue<AgentQueue>> agentInfoMap = new ConcurrentHashMap<>();
    /**
     * 通话中坐席
     */
    private Map<String, List<String>> callAgents = new ConcurrentHashMap<>();


    @Resource
    private TransferAgentProcessHandler transferAgentProcessHandler;
    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private OverFlowProcessHandler overFlowProcessHandler;
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private PlayBackCallHandler playBackCallHandler;
    @Resource
    private VdnProcessHandler vdnProcessHandler;

    /**
     * 初始化时启动定时任务
     */
    public GroupProcessHandler(DynamicTask dynamicTask){
        this.dynamicTask =dynamicTask;
        this.dynamicTask.startCron("GROUP_HANDLER_TASK",5,2, this::execute);
    }


    public void handler(CallInfo callInfo, GroupInfo groupInfo, String deviceId) {
        if (deviceId == null || groupInfo == null) {
            return;
        }
        log.info("callId:{} on groupId:{} groupName:{}", callInfo.getCallId(), groupInfo.getId(), groupInfo.getName());
        CallDetail joinGroup =CallDetail.builder()
                .callId(callInfo.getCallId())
                .startTime(new Date())
                .detailIndex(callInfo.getCallDetails().size() + 1)
                .transferType(3)
                .transferId(callInfo.getGroupId())
                .build();
        callInfo.getCallDetails().add(joinGroup);
        /**
         * 指定坐席
         */
        if (desiganteAgent(callInfo, groupInfo, deviceId)) {
            return;
        }
        /**
         * 走记忆坐席
         */
        if (memonryAgent(callInfo, groupInfo, deviceId)) {
            return;
        }
        String groupId = callInfo.getGroupId();
        //排队溢出策略
        GroupOverFlowInfo groupOverFlow = getEffectiveOverflow(groupInfo);
        if (groupOverFlow == null) {
            log.error("callId:{}, groupName:{} 无有效的溢出策略", callInfo.getCallId(), groupInfo.getName());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
            return;
        }
        log.info("callId:{}, group:{} overflow:{}", callInfo.getCallId(), groupInfo.getName(), groupOverFlow.getName());
        /**
         * 1:排队,2:溢出,3:挂机
         */
        switch (groupOverFlow.getHandleType()) {
            case 1:
                log.info("group:{} handleType is lineUp, queueTimeout:{}秒, busyType:{}, busyTimeoutType:{}, overflowType:{}, overflowValue:{}, callId:{}", groupInfo.getName(), groupOverFlow.getQueueTimeout(), groupOverFlow.getBusyType(), groupOverFlow.getBusyTimeoutType(), groupOverFlow.getOverflowType(), groupOverFlow.getOverflowValue(), callInfo.getCallId());
                PriorityQueue<CallQueue> callQueues = callInfoMap.get(groupId);
                if (callQueues == null) {
                    callQueues = new PriorityQueue<CallQueue>();
                }
                callInfo.setQueueStartTime(new Date());
                if (callInfo.getFristQueueTime() == null) {
                    callInfo.setFristQueueTime(callInfo.getQueueStartTime());
                }
                Long queueLevel = groupOverFlow.getLineupStrategy().calculateLevel(callInfo);
                callInfo.setQueueLevel(queueLevel);
                callQueues.add(CallQueue.builder()
                                .priority(callInfo.getQueueLevel())
                                .callId(callInfo.getCallId())
                                .startTime(callInfo.getQueueStartTime())
                                .groupId(groupId)
                                .groupOverflowInfo(groupOverFlow)
                                .deviceId(deviceId)
                                .build()
                );
                callInfoMap.put(callInfo.getGroupId(), callQueues);
                RedisService.getCallInfoManager().put(callInfo);
                break;

            case 2:
                overFlowProcessHandler.handler(callInfo, deviceId, groupOverFlow);
                break;
            case 3:
                log.info("group:{} handleType is hangup, callId:{}", groupInfo.getName(), callInfo.getCallId());
                //技能组策略挂机
                callInfo.setHangupDir(3);
                callInfo.setHangupCode(CauseEnums.OVERFLOW_TIMEOUT.getHuangupCode());
                hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                RedisService.getCallInfoManager().put(callInfo);
                break;
            default:
                break;
        }
    }

    /**
     * 指定坐席
     */
    private boolean desiganteAgent(CallInfo callInfo, GroupInfo groupInfo, String deviceId) {
        if (!callInfo.getProcessData().containsKey(Constant.DESIGNATE_AGENT)) {
            return false;
        }
        GroupMemoryConfigInfo groupMemoryConfig = groupInfo.getGroupMemoryConfig();
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(callInfo.getProcessData().get(Constant.DESIGNATE_AGENT).toString());
        if (agentVoInfo != null && agentVoInfo.getAgentState() == AgentStateEnum.READY) {
            log.info("callId:{} get desiganteAgent:{} on group:{}", callInfo.getCallId(), agentVoInfo.getAgentKey(), callInfo.getGroupId());
            //呼叫坐席
            callInfo.setQueueEndTime(new Date());
            CallDetail joinGroup = callInfo.getCallDetails().get(callInfo.getCallDetails().size() - 1);
            joinGroup.setEndTime(new Date());
            transferAgentProcessHandler.hanlder(callInfo, agentVoInfo, deviceId);
            agentNotReady(agentVoInfo);
            return true;
        }
        //没有空闲坐席，走匹配失败策略
        if (groupMemoryConfig == null) {
            return false;
        }
        switch (groupMemoryConfig.getFailStrategy()) {
            case 1:
                //转其他空闲坐席
                return false;
            case 2:
                //转其他技能组
                handler(callInfo, RedisService.getGroupInfoManager().get(groupMemoryConfig.getFailStrategyValue()), deviceId);
                return true;
            case 3://vdn
                break;
            case 4://ivr
                break;
            case 5://挂机
                hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                return true;
            default:
                break;
        }
        return false;
    }
    /**
     * 呼入记忆坐席
     */
    private boolean memonryAgent(CallInfo callInfo, GroupInfo groupInfo, String deviceId) {
        GroupMemoryConfigInfo groupMemoryConfig = groupInfo.getGroupMemoryConfig();
        if (groupMemoryConfig == null || groupInfo.getGroupMemoryConfig().getStatus() != 1) {
            return false;
        }
        GroupMemoryInfo groupMemory = FsService.getGroupMemoryInfoService().find(groupInfo.getId(), callInfo.getCaller());
        if (groupMemory == null) {
            switch (groupMemoryConfig.getFailStrategy()) {
                case 1:
                    return false;
                case 2:
                    handler(callInfo, RedisService.getGroupInfoManager().get(groupMemoryConfig.getFailStrategyValue()), deviceId);
                    return true;
                case 5://挂机
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                    return true;
                default:
                    return false;
            }
        }
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(groupMemory.getAgentKey());
        if (agentVoInfo != null && agentVoInfo.getAgentState() == AgentStateEnum.READY) {
            transferAgentProcessHandler.hanlder(callInfo, agentVoInfo, deviceId);
            agentNotReady(agentVoInfo);
            return true;
        }
        //匹配上了记忆坐席
        switch (groupMemoryConfig.getSuccessStrategy()) {
            case 1:
                //一直等待记忆坐席(一直放音)
                return true;
            case 2:
                //超时转空闲坐席(放音一段时间后转其他空闲坐席)
                return true;
            case 3:
                //忙碌转空闲坐席
                break;
            default:
                return false;
        }
        return false;
    }

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
    /************************************************定时运行策略********************************************************************/
    /**
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
                        playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).playPath("queue.wav").build());
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
                        playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).playPath("queue.wav").build());
                    }
                    callInfo.setQueueEndTime(date);
                    transferAgentProcessHandler.hanlder(callInfo, agentVoInfo, callQueue.getDeviceId());
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
        playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callQueue.getDeviceId()).isDown(true).build());
        vdnProcessHandler.doNextCommand(callInfo, deviceInfo, nextCommand);
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
        if (agentVoInfo == null || agentVoInfo.getAgentState() !=AgentStateEnum.READY) {
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


}
