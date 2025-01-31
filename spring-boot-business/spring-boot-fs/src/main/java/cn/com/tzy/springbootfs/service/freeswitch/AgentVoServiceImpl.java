package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootentity.dome.fs.AgentSip;
import cn.com.tzy.springbootfs.convert.fs.AgentConvert;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootfs.mapper.fs.AgentSipMapper;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AgentVoServiceImpl extends AgentVoService {

    @Resource
    private AgentMapper agentMapper;
    @Resource
    private AgentService agentService;
    @Resource
    private AgentSipMapper agentSipMapper;
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    @Resource
    protected SipServer sipServer;

    @Override
    public AgentVoInfo getAgentBySip(String sip) {
        Agent agent = agentMapper.getAgentBySip(sip);
        return getAgentVoInfo(agent);
    }

    @Override
    public AgentVoInfo getAgentByKey(String agentKey) {
        Agent agent = agentMapper.selectOne(new LambdaQueryWrapper<Agent>().eq(Agent::getAgentKey, agentKey));
        return getAgentVoInfo(agent);
    }

    @Override
    public void stopStream(String callId) {
        agentService.stopStream(callId);
    }

    /**
     * 生成语音视频流，再对方回复来后，需要给对方推送
     * 获取推流地址
     */
    @Override
    public RestResult<?> pushWebRtp(VideoStreamType type,String agentKey) {
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
        String streamId = String.format("%s:%s",type.getPushName(),agentKey);
        SendRtp sendRtp = sendRtpManager.querySendRTPServer(agentKey, streamId, null);
        if(sendRtp != null){
            if(StringUtils.isEmpty(sendRtp.getCallId())){
                return RestResult.result(RespCode.CODE_2.getValue(),"语音流正在开启中");
            }if(agentKey.equals(sendRtp.getAgentKey())){
                return RestResult.result(RespCode.CODE_2.getValue(),"语音通话中,请稍后");
            }else {
                return RestResult.result(RespCode.CODE_2.getValue(),"每个设备只能一个通道语音对讲");
            }
        }
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if(agentVoInfo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取客服信息");
        }
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentVoInfo);
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取流媒体信息");
        }
        SendRtp build = SendRtp.builder()
                .agentKey(agentKey)
                .pushStreamId(streamId)
                .callId(null)
                .mediaServerId(mediaServerVo.getId())
                .audioInfo(SendRtp.createSendRtpInfo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        VideoStreamType.RTP_STREAM.getCallName(),
                        streamId,
                       null,
                       null,
                        null,
                        sipServer.getVideoProperties().getServerId(),
                        true,
                        null))
                .build();
        if(type.getPushName().equals(VideoStreamType.CALL_VIDEO_PHONE.getPushName())){
            build.setVideoInfo(SendRtp.createSendRtpInfo(
                    null,
                    null,
                    null,
                    null,
                    null,
                    VideoStreamType.RTP_STREAM.getCallName(),
                    streamId,
                    null,
                    null,
                    null,
                    sipServer.getVideoProperties().getServerId(),
                    true,
                    null));
        }
        sendRtpManager.put(build);
        dynamicTask.startDelay(streamId,15,()->{
            sendRtpManager.deleteSendRTPServer(build.getAgentKey(), build.getPushStreamId(), build.getCallId());
            if(StringUtils.isEmpty(build.getCallId())){// 如何没有callId表示没有接收到Invite请求 则直接关闭
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,null,null,build.getCallId(),type.getCallName(),null,null);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败] 语音流 发送BYE: {}", e.getMessage());
                }
            }
        });
        //开始拨打电话
        HookKey hookKey = HookKeyFactory.onStreamChanged(VideoStreamType.RTP_STREAM.getCallName(), streamId, true, "rtsp", mediaServerVo.getId());
        mediaHookSubscribe.addSubscribe(hookKey,(MediaServerVo mediaServer, HookVo response)->{
            FsService.getSendAgentMessage().sendMessage(AgentCommon.SOCKET_AGENT,AgentCommon.AGENT_OUT_PUSH_PATH,build.getAgentKey(),RestResult.result(RespCode.CODE_0.getValue(),"推流接收成功",null));
            //dynamicTask.stop(streamId); // 在INVITE响应200后关闭 超时回调
            mediaHookSubscribe.removeSubscribe(hookKey);
        });
        //String audioPushPath = String.format("%s://%s:%s%s/index/api/webrtc?app=%s&stream=%s&type=push",mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?"http":"https",mediaServerVo.getStreamIp(), mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?mediaServerVo.getHttpPort():mediaServerVo.getHttpSslPort(), StringUtils.isNotEmpty(mediaServerVo.getVideoHttpPrefix())?String.format("/%s",mediaServerVo.getVideoHttpPrefix()):"",app, streamId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,null);
    }


    @Override
    public AgentVoInfo getAgentByCompanyCode(String company, String agentCode) {
        Agent agent = agentMapper.selectOne(new LambdaQueryWrapper<Agent>().eq(Agent::getCompanyId,company).eq(Agent::getAgentCode, agentCode));
        return getAgentVoInfo(agent);
    }

    @Override
    public AgentVoInfo findAgentId(String id) {
        Agent agent = agentMapper.selectById(id);
        return getAgentVoInfo(agent);
    }

    @Override
    public void save(AgentVoInfo entity) {
        Agent agent = AgentConvert.INSTANCE.convert(entity);
        if(agent.getId() != null){
            agentMapper.updateById(agent);
        }else {
            agentMapper.insert(agent);
        }

    }

    @Override
    public void updateStatus(Long id, boolean b) {
        Agent agent = agentMapper.selectById(id);
        Agent build = Agent.builder()
                .id(agent.getId())
                .host("")
                .state(b ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue())
                .registerTime(b ?new Date():null)
                .renewTime(b ?new Date():null)
                .keepaliveTime(b ?new Date():null)
                .build();
        agentMapper.updateById(build);
    }

    @Override
    public void startPlay(String agentCode, String stream) {

    }

    @Override
    public void stopPlay(String agentCode) {

    }

    private AgentVoInfo getAgentVoInfo(Agent agent) {
        if(agent == null){
            return null;
        }
        List<AgentSip> agentSips = agentSipMapper.selectList(Wrappers.<AgentSip>lambdaQuery().in(AgentSip::getAgentId, agent.getId()));
        if(!agentSips.isEmpty()){
            agent.setSipPhoneList(agentSips.stream().map(AgentSip::getSip).collect(Collectors.toList()));
        }
        AgentVoInfo convert = AgentConvert.INSTANCE.convert(agent);
        convert.setAgentOnline(ConstEnum.Flag.NO.getValue());
        convert.setSsrcCheck(ConstEnum.Flag.NO.getValue());
        return convert;
    }
}
