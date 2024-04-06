package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.vo.media.HookKey;
import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SendRtp;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.pool.task.DynamicTask;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.SendRtpManager;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipRequestEvent;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import java.text.ParseException;

/**
 * SIP命令类型： ACK请求
 * 给上级平台进行推流
 */
@Log4j2
public class AckRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {

    @Resource
    private MediaHookSubscribe mediaHookSubscribe;
    @Resource
    private DeferredResultHolder deferredResultHolder;

    @Override
    public String getMethod() {
        return Request.ACK;
    }

    /**
     * 处理  ACK请求
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        DynamicTask dynamicTask = SpringUtil.getBean(DynamicTask.class);
        CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
        String platformGbId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(FromHeader.NAME)).getAddress().getURI()).getUser();
        String channelId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();
        log.info("[收到ACK]： platformGbId->{}", platformGbId);
        //业务开始 获取相关实现业务接口
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        if(deviceVoService == null){
            log.error(" DeviceService not implements ");
            return;
        }
        MediaServerVoService mediaServerVoService = VideoService.getMediaServerService();
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        // 取消设置的超时任务
        dynamicTask.stop(callIdHeader.getCallId());
        SendRtp sendRtpItem = sendRtpManager.querySendRTPServer(null, null, null, callIdHeader.getCallId());
        if (sendRtpItem == null) {
            log.warn("[收到ACK]：未找到通道({})的推流信息", channelId);
            return;

        }
        String is_Udp = sendRtpItem.isTcp() ? "0" : "1";
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtpItem.getMediaServerId());
        if(mediaServerVo == null){
            log.warn("[收到ACK]：未找到流媒体({})的信息", sendRtpItem.getMediaServerId());
            return;
        }
        // 如果是非严格模式，需要关闭端口占用
        if (sendRtpItem.getLocalPort() != 0) {
            MediaClient.closeRtpServer(mediaServerVo,sendRtpItem.getStreamId());
            HookKey hookKey = HookKeyFactory.onRtpServerTimeout(sendRtpItem.getSsrc(), mediaServerVo.getId());
            // 订阅 zlm启动事件, 新的zlm也会从这里进入系统
            mediaHookSubscribe.removeSubscribe(hookKey);
        }
        if("audio".equals(sendRtpItem.getApp())){
            String key = String.format("%s%s_%s", DeferredResultHolder.CALLBACK_CMD_BROADCAST,sendRtpItem.getDeviceId(),sendRtpItem.getChannelId());
            deferredResultHolder.invokeAllResult(key, RestResult.result(RespCode.CODE_0.getValue(),"语音流加载成功。",null));
        }
        // tcp主动时，此时是级联下级平台，在回复200ok时，本地已经请求zlm开启监听，跳过下面步骤
        if (sendRtpItem.isTcpActive()) {
            log.info("收到ACK，rtp/{} 对方主动模式 无需推流, SSRC={}", sendRtpItem.getStreamId(),  sendRtpItem.getSsrc());
            return;
        }
        log.info("收到ACK，rtp/{}开始向上级推流, 目标={}:{}，SSRC={}", sendRtpItem.getStreamId(), sendRtpItem.getIp(), sendRtpItem.getPort(), sendRtpItem.getSsrc());
        MediaRestResult restResult  = MediaClient.startSendRtp(
                mediaServerVo,
                "__defaultVhost__",
                sendRtpItem.getApp(),
                sendRtpItem.getStreamId(),
                sendRtpItem.getSsrc(),
                sendRtpItem.getIp(),
                sendRtpItem.getPort(),
                is_Udp,
                sendRtpItem.getLocalPort(),
                sendRtpItem.getPt(),
                sendRtpItem.isUsePs() ? 1 : 0,
                sendRtpItem.isOnlyAudio() ? 1 : 0,
                sendRtpItem.isTcp()?(sendRtpItem.isRtcp() ? 1 : 0):null
        );
        if (restResult != null) {
            startSendRtpStreamHand(sendRtpItem,platformGbId, restResult);
        }
    }

    private void startSendRtpStreamHand(SendRtp sendRtpItem, String platformGbId, MediaRestResult restResult) {
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        if (restResult == null || restResult.getCode() != RespCode.CODE_0.getValue()) {
            sendRtpItem.setStatus(3);
            if(restResult == null){
                log.error("RTP推流失败: 请检查ZLM服务");
            }else {
                log.error("RTP推流失败: {}, 参数：{}",restResult.getMsg(), JSONUtil.toJsonPrettyStr(sendRtpItem));
            }
            ParentPlatformVo parentPlatformVo = VideoService.getParentPlatformService().getParentPlatformByServerGbId(platformGbId);
            DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(platformGbId);
            if(parentPlatformVo != null){
                // 向上级平台
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, parentPlatformVo,sendRtpItem,null,null);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
                }
            }else if(deviceVo != null){
                // 向上级平台
                try {
                    sipCommander.streamByeCmd(sipServer, deviceVo,sendRtpItem.getChannelId(),sendRtpItem.getStreamId(),sendRtpItem.getCallId(),null,null,null);
                } catch (SipException | InvalidArgumentException | ParseException | SsrcTransactionNotFoundException e) {
                    log.error("[命令发送失败] 设备通道 发送BYE: {}", e.getMessage());
                }
            }
        } else {
            sendRtpItem.setStatus(2);
            log.info("调用ZLM推流接口, 结果： {}",  restResult.toString());
            log.info("RTP推流成功[ {}/{} ]，{}->{}:{}, " ,sendRtpItem.getApp(), sendRtpItem.getStreamId(), sendRtpItem.getLocalPort(), sendRtpItem.getIp(), sendRtpItem.getPort());
        }
        sendRtpManager.put(sendRtpItem);
    }
}
