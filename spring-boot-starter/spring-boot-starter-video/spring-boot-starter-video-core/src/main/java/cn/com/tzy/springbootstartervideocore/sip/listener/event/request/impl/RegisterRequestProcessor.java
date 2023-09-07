package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstartervideobasic.enums.*;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.SipTransactionInfo;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.SipTransactionManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.sip.auth.DigestServerAuthenticationHelper;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipRequestEvent;
import cn.com.tzy.springbootstartervideocore.utils.SipUtils;
import cn.com.tzy.springbootstartervideocore.utils.VideoSipDate;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;

import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * SIP命令类型： REGISTER请求  设备注册请求
 */
@Log4j2
public class RegisterRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {
    @Override
    public String getMethod() {
        return Request.REGISTER;
    }

    @Override
    public void process(RequestEvent event) {
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        SipTransactionManager sipTransactionManager = RedisService.getSipTransactionManager();
        try {
            RequestEventExt evtExt = (RequestEventExt) event;
            String requestAddress = String.format("%s:%s",evtExt.getRemoteIpAddress(),evtExt.getRemotePort());
            SIPRequest request = (SIPRequest)event.getRequest();
            // 注册标志
            boolean registerFlag = request.getExpires().getExpires() > 0;
            String title = registerFlag ? "[注册请求]": "[注销请求]";
            FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
            AddressImpl address = (AddressImpl) fromHeader.getAddress();
            SipUri uri = (SipUri) address.getURI();
            String deviceId = uri.getUser();
            DeviceVo deviceVo = deviceVoService.findDeviceGbId(deviceId);
            Address remoteAddress = SipUtils.getRemoteAddressFromRequest(request, videoProperties.getSipUseSourceIpAsRemoteAddress());
            SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
            Response response;
            log.info("[{}] 设备：{}, 开始处理: {}",title, deviceId, remoteAddress);
            if (deviceVo != null && registerFlag) {
                SipTransactionInfo sipTransactionInfo = sipTransactionManager.findDevice(deviceVo.getDeviceId());
                if(sipTransactionInfo != null && request.getCallIdHeader().getCallId().equals(sipTransactionInfo.getCallId())){
                    log.info("[{}] 注册续订: {}",title, deviceVo.getDeviceId());
                    deviceVo.setExpires(request.getExpires().getExpires());
                    deviceVo.setIp(remoteAddress.getIp());
                    deviceVo.setPort(remoteAddress.getPort());
                    deviceVo.setHostAddress(remoteAddress.getIp().concat(":").concat(String.valueOf(remoteAddress.getPort())));
                    deviceVo.setLocalIp(request.getLocalAddress().getHostAddress());
                    response = getRegisterOkResponse(request);
                    // 判断TCP还是UDP
                    ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                    String transport = reqViaHeader.getTransport();
                    deviceVo.setTransport("TCP".equalsIgnoreCase(transport) ? TransportType.TCP.getValue() :TransportType.UDP.getValue());
                    sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),response);
                    deviceVo.setRegisterTime(new Date());
                    deviceVoService.online(deviceVo,sipServer,sipCommander,videoProperties,new SipTransactionInfo((SIPResponse)response));
                    return;
                }
            }
            //设备密码
            String password = (deviceVo != null && !ObjectUtils.isEmpty(deviceVo.getPassword()))? deviceVo.getPassword() : sipConfigProperties.getPassword();
            AuthorizationHeader authHead = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);

            if (authHead == null && !ObjectUtils.isEmpty(password)) {
                log.info("[{}] 回复401: {}",title, requestAddress);
                response = sipServer.getSipFactory().createMessageFactory().createResponse(Response.UNAUTHORIZED, request);
                new DigestServerAuthenticationHelper().generateChallenge(sipServer.getSipFactory().createHeaderFactory(), response, sipConfigProperties.getDomain());
                //回复未注册消息 401
                sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),response);
                return;
            }
            // 校验密码是否正确
            boolean passwordCorrect = ObjectUtils.isEmpty(password) || new DigestServerAuthenticationHelper().doAuthenticatePlainTextPassword(request, password);
            if (!passwordCorrect) {
                // 注册失败
                response = sipServer.getSipFactory().createMessageFactory().createResponse(Response.FORBIDDEN, request);
                response.setReasonPhrase("wrong password");
                log.info("[{}] 密码/SIP服务器ID错误, 回复403: {}",title, requestAddress);
                sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(), response);
                return;
            }
            if (request.getExpires() == null) {//无过期时间回复参数错误
                response = sipServer.getSipFactory().createMessageFactory().createResponse(Response.BAD_REQUEST, request);
                sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(), response);
                return;
            }
            response = getRegisterOkResponse(request);
            Address remoteAddressFromRequest = SipUtils.getRemoteAddressFromRequest(request, videoProperties.getSipUseSourceIpAsRemoteAddress());
            if (deviceVo == null) {
                deviceVo = new DeviceVo();
                deviceVo.setStreamMode(StreamModeType.UDP.getValue());
                deviceVo.setCharset(CharsetType.GB2312.getValue());
                deviceVo.setGeoCoordSys(GeoCoordSysType.WGS84.getValue());
                deviceVo.setTreeType(GbIdConstant.Type.TYPE_215.getValue());
                deviceVo.setDeviceId(deviceId);
                deviceVo.setOnline(ConstEnum.Flag.NO.getValue());
            }else {
                if (ObjectUtils.isEmpty(deviceVo.getStreamMode())) {
                    deviceVo.setStreamMode(StreamModeType.UDP.getValue());
                }
                if (ObjectUtils.isEmpty(deviceVo.getCharset())) {
                    deviceVo.setCharset(CharsetType.GB2312.getValue());
                }
                if (ObjectUtils.isEmpty(deviceVo.getGeoCoordSys())) {
                    deviceVo.setGeoCoordSys(GeoCoordSysType.WGS84.getValue());
                }
            }
            deviceVo.setIp(remoteAddressFromRequest.getIp());
            deviceVo.setPort(remoteAddressFromRequest.getPort());
            deviceVo.setHostAddress(remoteAddressFromRequest.getIp().concat(":").concat(String.valueOf(remoteAddressFromRequest.getPort())));
            deviceVo.setLocalIp(request.getLocalAddress().getHostAddress());
            sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(), response);
            // 注册成功
            if (registerFlag) {
                log.info("[{}] deviceId: {}->{}",title,  deviceId, requestAddress);
                // 注册成功
                deviceVo.setExpires(request.getExpires().getExpires());
                // 判断TCP还是UDP
                ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                String transport = reqViaHeader.getTransport();
                deviceVo.setTransport("TCP".equalsIgnoreCase(transport) ? 2 : 1);
                deviceVo.setRegisterTime(new Date());
                deviceVoService.online(deviceVo,sipServer,sipCommander,videoProperties,new SipTransactionInfo((SIPResponse)response));
            } else {
                log.info("[{}] deviceId: {}->{}",title ,deviceId, requestAddress);
                deviceVoService.offline(deviceId);
            }
        }catch (NoSuchAlgorithmException | SipException | ParseException e){
            log.error(e.getMessage());
        }catch (Exception e){
            log.error("[REGISTER请求]，消息处理异常：", e );
        }
    }

    private Response getRegisterOkResponse(Request request) throws ParseException, PeerUnavailableException {
        // 携带授权头并且密码正确
        Response  response = sipServer.getSipFactory().createMessageFactory().createResponse(Response.OK, request);
        // 添加date头
        SIPDateHeader dateHeader = new SIPDateHeader();
        dateHeader.setDate( new VideoSipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis()));
        response.addHeader(dateHeader);
        // 添加Contact头
        response.addHeader(request.getHeader(ContactHeader.NAME));
        // 添加Expires头
        response.addHeader(request.getExpires());
        return response;

    }
}
