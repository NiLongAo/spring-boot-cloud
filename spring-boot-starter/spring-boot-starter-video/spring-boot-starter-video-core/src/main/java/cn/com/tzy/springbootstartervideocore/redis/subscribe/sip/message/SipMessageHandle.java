package cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.message;


import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.demo.MessageTypeVo;
import cn.com.tzy.springbootstartervideocore.model.EventResult;
import cn.com.tzy.springbootstartervideocore.model.RestResultEvent;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.utils.SipLogUtils;
import cn.com.tzy.springbootstartervideocore.utils.SipUtils;
import cn.hutool.core.codec.Base64;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.stack.SIPClientTransactionImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class SipMessageHandle extends AbstractMessageListener {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;


    public SipMessageHandle() {
        super(VideoConstant.VIDEO_SEND_SIP_MESSAGE);
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        try {
            SipServer sipServer = SpringUtil.getBean(SipServer.class);
            Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
            Object deserialize = SerializationUtils.deserialize(Base64.decode((String) body));
            MessageTypeVo vo = (MessageTypeVo) deserialize;
            if(MessageTypeVo.TypeEnum.DEVICE.getValue()== (vo.getType())){
                Address address = RedisService.getRegisterServerManager().getDevice(vo.getGbId());
                DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(vo.getGbId());
                if(address != null && deviceVo != null &&  nacosDiscoveryProperties.getIp().equals(address.getIp()) &&  nacosDiscoveryProperties.getPort() == address.getPort()){
                    String localIp = sipServer.getLocalIp(deviceVo.getLocalIp());
                    handleMessage(localIp,vo.getMessage());
                }else {
                    log.error("[SIP接收消息] [设备] 未获取注册地址 gbId : {}",vo.getGbId());
                    sendErrorMsg(sipServer, vo.getMessage(), String.format("未获取设备注册地址 国标编号 :%s",vo.getGbId()));
                }
            }else if(MessageTypeVo.TypeEnum.PLATFORM.getValue()== (vo.getType())){
                Address address = RedisService.getRegisterServerManager().getPlatform(vo.getGbId());
                ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(vo.getGbId());
                if(address != null && platform != null &&  nacosDiscoveryProperties.getIp().equals(address.getIp()) &&  nacosDiscoveryProperties.getPort() == address.getPort()){
                    String localIp = sipServer.getLocalIp(platform.getDeviceIp());
                    handleMessage(localIp,vo.getMessage());
                }else {
                    log.error("[SIP接收消息] [国标级联] 未获取注册地址 gbId : {}",vo.getGbId());
                    sendErrorMsg(sipServer, vo.getMessage(), String.format("未获取国标级联注册地址 国标编号 :%s",vo.getGbId()));
                }
            }else {
                log.error("[SIP接收消息] 类型错误:{}", JSONUtil.toJsonStr(vo));
                sendErrorMsg(sipServer, vo.getMessage(), String.format("消息类型错误 :%s",JSONUtil.toJsonStr(vo)));
            }
        }catch (Exception e){
            log.error("[SIP接收消息] 发生错误:", e);
        }
    }
    /**
     * @param ip        发送端SIP IP
     * @param message   消息体
     * @throws SipException
     */
    public void handleMessage(String ip, Message message) throws SipException {
        SipServer sipServer = SpringUtil.getBean(SipServer.class);
        ViaHeader viaHeader = (ViaHeader) message.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (viaHeader == null) {
            log.warn("[消息头缺失]： ViaHeader， 使用默认的UDP方式处理数据");
        } else {
            transport = viaHeader.getTransport();
        }
        if (message.getHeader(UserAgentHeader.NAME) == null) {
            try {
                message.addHeader(SipUtils.createUserAgentHeader(sipServer.getSipFactory()));
            } catch (ParseException e) {
                sendErrorMsg(sipServer, message, "添加UserAgentHeader失败");
                log.error("添加UserAgentHeader失败", e);
            }
        }
        //打印日志
        SipLogUtils.sendMessage(sipServer,message);
        if ("TCP".equals(transport)) {
            SipProviderImpl tcpSipProvider = sipServer.getTcpSipProvider(ip);
            if (tcpSipProvider == null) {
                log.error("[发送信息失败] 未找到tcp://{}的监听信息", ip);
                sendErrorMsg(sipServer, message, String.format("未找到tcp://%s的监听信息",ip));
                return;
            }
            sendSip(tcpSipProvider, message);
        } else if ("UDP".equals(transport)) {
            SipProviderImpl sipProvider = sipServer.getUdpSipProvider(ip);
            if (sipProvider == null) {
                log.error("[发送信息失败] 未找到udp://{}的监听信息", ip);
                sendErrorMsg(sipServer, message, String.format("未找到udp://%s的监听信息",ip));
                return;
            }
            sendSip(sipProvider, message);
        }
    }

    private static void sendSip(SipProviderImpl sipProvider, Message message) throws SipException {
        if (message instanceof Request) {
            List<String> methodList = Arrays.asList(Request.ACK,Request.BYE);
            if(methodList.contains(((SIPRequest)message).getMethod())){
                sipProvider.sendRequest((Request)message);
            }else {
                SIPClientTransactionImpl newClientTransaction = (SIPClientTransactionImpl)sipProvider.getNewClientTransaction((Request) message);
                //开启超时机制 设置500 是为了取消重试机制
                //目前机制 为 发送消息后 32秒未收到回应则触发 processTimeout 超时机制
                newClientTransaction.setTimerT2(-500);
                newClientTransaction.setRetransmitTimer(200);//200毫秒
                newClientTransaction.sendRequest();
            }
        } else if (message instanceof Response) {
            sipProvider.sendResponse((Response) message);
        }
    }

    private void sendErrorMsg(SipServer sipServer,Message message,String error){
        if(message == null){
            log.error("[发送信息失败] 发送错误消息时,未获取消息主体");
            return;
        }
        CallIdHeader callIdHeader = (CallIdHeader) message.getHeader(CallIdHeader.NAME);
        SipSubscribeHandle sipSubscribeHandle = sipServer.getSubscribeManager();
        SipSubscribeEvent errorSubscribe = sipSubscribeHandle.getErrorSubscribe(callIdHeader.getCallId());
        if(errorSubscribe !=null){
            errorSubscribe.response(new EventResult<RestResultEvent>(new RestResultEvent(RespCode.CODE_2.getValue(),error)));
        }
    }

}
