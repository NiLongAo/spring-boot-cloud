package cn.com.tzy.springbootstartervideocore.config.sip;

import cn.com.tzy.springbootstartervideocore.config.runner.SipDeviceRunner;
import cn.com.tzy.springbootstartervideocore.config.runner.SipPlatformRunner;
import cn.com.tzy.springbootstartervideocore.config.runner.SipServerRunner;
import cn.com.tzy.springbootstartervideocore.config.runner.ZLMRunner;
import cn.com.tzy.springbootstartervideocore.media.client.ZlmService;
import cn.com.tzy.springbootstartervideocore.media.hook.MediaHookServer;
import cn.com.tzy.springbootstartervideocore.pool.task.DynamicTask;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.redis.impl.*;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.notify.DeviceNotifyHandle;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.record.RecordEndSubscribeHandle;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.message.SipMessageHandle;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.register.SipRegisterHandle;
import cn.com.tzy.springbootstartervideocore.service.PlayService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.impl.SIPCommanderFroPlatformImpl;
import cn.com.tzy.springbootstartervideocore.sip.cmd.impl.SIPCommanderImpl;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.*;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageRequestProcessor;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.control.ControlMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.control.cmd.DeviceControlQueryMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.NotifyMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd.AlarmNotifyMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd.KeepaliveNotifyMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd.MediaStatusNotifyMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd.MobilePositionNotifyMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.QueryMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.cmd.*;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.ResponseMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.cmd.*;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.response.impl.ByeResponseProcessor;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.response.impl.CancelResponseProcessor;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.response.impl.InviteResponseProcessor;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.response.impl.RegisterResponseProcessor;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.timeout.impl.SipTimeoutEventImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * SIP注册中心
 */
@Import({
        SipServer.class, DeferredResultHolder.class,
        ZlmService.class, MediaHookServer.class, MediaHookSubscribe.class,
        PlatformRegisterManager.class, SendRtpManager.class, SipTransactionManager.class, SsrcConfigManager.class, SsrcTransactionManager.class, MediaServerManager.class, InviteStreamManager.class, CseqManager.class,RegisterServerManager.class, PlatformNotifySubscribeManager.class, CatalogDataManager.class,StreamChangedManager.class,RecordMp4Manager.class,
        SipMessageHandle.class, SipRegisterHandle.class,
        MessageRequestProcessor.class,AckRequestProcessor.class, ByeRequestProcessor.class, CancelRequestProcessor.class, InfoRequestProcessor.class, InviteRequestProcessor.class, NotifyRequestProcessor.class, RegisterRequestProcessor.class, SubscribeRequestProcessor.class,
        ControlMessageHandler.class, DeviceControlQueryMessageHandler.class,
        NotifyMessageHandler.class, AlarmNotifyMessageHandler.class, KeepaliveNotifyMessageHandler.class, MediaStatusNotifyMessageHandler.class, MobilePositionNotifyMessageHandler.class,
        QueryMessageHandler.class, AlarmQueryMessageHandler.class, CatalogQueryMessageHandler.class, DeviceInfoQueryMessageHandler.class, DeviceStatusQueryMessageHandler.class, RecordInfoQueryMessageHandler.class,
        ResponseMessageHandler.class, AlarmResponseMessageHandler.class, BroadcastResponseMessageHandler.class, CatalogResponseMessageHandler.class, DeviceConfigResponseMessageHandler.class, ConfigDownloadResponseMessageHandler.class, DeviceControlResponseMessageHandler.class, DeviceInfoResponseMessageHandler.class, DeviceStatusResponseMessageHandler.class, MobilePositionResponseMessageHandler.class, PresetQueryResponseMessageHandler.class, RecordInfoResponseMessageHandler.class,
        ByeResponseProcessor.class, CancelResponseProcessor.class, InviteResponseProcessor.class, RegisterResponseProcessor.class,
        SipServerRunner.class, ZLMRunner.class,SipPlatformRunner.class, SipDeviceRunner.class,
        SipTimeoutEventImpl.class,
        SipSubscribeHandle.class, RecordEndSubscribeHandle.class, DeviceNotifyHandle.class, DeviceNotifySubscribeManager.class,
        DynamicTask.class, VideoProperties.class, SipConfigProperties.class,PlayService.class,
        SIPCommanderImpl.class, SIPCommanderFroPlatformImpl.class,
})
@Configuration
@RequiredArgsConstructor
public class SipConfig {

}
