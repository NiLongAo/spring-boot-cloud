package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstartervideobasic.enums.InviteStreamType;
import cn.com.tzy.springbootstartervideobasic.enums.VideoStreamType;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SendRtp;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.demo.SsrcTransaction;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.InviteStreamManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SendRtpManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcConfigManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcTransactionManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceChannelVoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipRequestEvent;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Log4j2
public class ByeRequestProcessor  extends AbstractSipRequestEvent implements SipRequestEvent {


    @Override
    public String getMethod() {return Request.BYE;}

    @Override
    public void process(RequestEvent evt) {
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[回复BYE信息失败]，{}", e.getMessage());
        }
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        MediaServerVoService mediaServerVoService = VideoService.getMediaServerService();
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        DeviceChannelVoService deviceChannelVoService = VideoService.getDeviceChannelService();

        CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
        String platformGbId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(FromHeader.NAME)).getAddress().getURI()).getUser();
        String channelId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();

        SendRtp sendRtpItem =  sendRtpManager.querySendRTPServer(platformGbId, channelId, null, callIdHeader.getCallId());
        log.info("[收到bye] {}/{}", platformGbId, channelId);
        if (sendRtpItem != null){
            log.info("[收到bye] 停止向上级推流：{}", sendRtpItem.getStreamId());
            MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtpItem.getMediaServerId());
            sendRtpManager.deleteSendRTPServer(platformGbId, channelId, null, callIdHeader.getCallId());
            //关闭推流
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),sendRtpItem.getSsrc());
            MediaClient.stopSendRtp(mediaServerVo,"__defaultVhost__",sendRtpItem.getApp(),sendRtpItem.getStreamId(),sendRtpItem.getSsrc());
            MediaRestResult result = MediaClient.getMediaInfo(mediaServerVo,"__defaultVhost__", "rtsp", sendRtpItem.getApp(), sendRtpItem.getStreamId());
            int totalReaderCount = 0;
            if(result != null && result.getCode() == RespCode.CODE_0.getValue() && ObjectUtils.isNotEmpty(result.getData())){
                OnStreamChangedHookVo hookVo = BeanUtil.toBean(result.getData(), OnStreamChangedHookVo.class);
                if(hookVo != null){
                    totalReaderCount =hookVo.getTotalReaderCount();
                }
            }
            if (totalReaderCount <= 0) {
                log.info("[收到bye] {} 无其它观看者，通知设备停止推流", sendRtpItem.getStreamId());
                if (sendRtpItem.getPlayType().equals(InviteStreamType.PLAY)) {
                    DeviceVo deviceVo = deviceVoService.findDeviceGbId(sendRtpItem.getDeviceId());
                    if (deviceVo == null) {
                        log.info("[收到bye] {} 通知设备停止推流时未找到设备信息", sendRtpItem.getStreamId());
                    }
                    try {
                        log.info("[停止点播] {}/{}", sendRtpItem.getDeviceId(), channelId);
                        sipCommander.streamByeCmd(sipServer, deviceVo, channelId, sendRtpItem.getStreamId(), "play",null,null,null);
                    } catch (InvalidArgumentException | ParseException | SipException |
                             SsrcTransactionNotFoundException e) {
                        log.error("[收到bye] {} 无其它观看者，通知设备停止推流， 发送BYE失败 {}",sendRtpItem.getStreamId(), e.getMessage());
                    }
                }
//                if (sendRtpItem.getPlayType().equals(InviteStreamType.PUSH)) {
//                    MessageForPushChannel messageForPushChannel = MessageForPushChannel.getInstance(0,
//                            sendRtpItem.getApp(), sendRtpItem.getStreamId(), sendRtpItem.getChannelId(),
//                            sendRtpItem.getPlatformId(), null, null, sendRtpItem.getMediaServerId());
//                    redisCatchStorage.sendStreamPushRequestedMsg(messageForPushChannel);
//                }
            }
        }else {
            // 可能是设备发送的停止
            SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(null, null, callIdHeader.getCallId(), null,null);
            if (ssrcTransaction == null) {
                return;
            }
            log.info("[收到bye] 来自设备：{}, 通道已停止推流: {}", ssrcTransaction.getDeviceId(), ssrcTransaction.getChannelId());
            DeviceVo deviceVo = deviceVoService.findDeviceGbId(ssrcTransaction.getDeviceId());
            if (deviceVo == null) {
                log.info("[收到bye] 未找到设备：{} ", ssrcTransaction.getDeviceId());
                return;
            }
            DeviceChannelVo deviceChannelVo = deviceChannelVoService.findChannelId(platformGbId);
            if(deviceChannelVo == null){
                log.info("[收到bye] 未找到通道，设备：{}， 通道：{}", ssrcTransaction.getDeviceId(), ssrcTransaction.getChannelId());
                return;
            }
            deviceChannelVoService.stopPlay(deviceVo.getDeviceId(), channelId);
            InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByDeviceAndChannel(VideoStreamType.play, deviceVo.getDeviceId(), channelId);
            if (inviteInfo != null) {
                inviteStreamManager.removeInviteInfo(inviteInfo);
                if (inviteInfo.getStreamInfo() != null) {
                    MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(inviteInfo.getStreamInfo().getMediaServerId());
                    if(mediaServerVo != null){
                        MediaClient.closeRtpServer(mediaServerVo,inviteInfo.getStream());
                    }
                }
            }
            ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(),ssrcTransaction.getSsrc());
            ssrcTransactionManager.remove(deviceVo.getDeviceId(), channelId, ssrcTransaction.getStream());
        }
    }
}
