package cn.com.tzy.springbootstarterfreeswitch.client.sip.utils;

import cn.com.tzy.springbootstarterfreeswitch.vo.sip.Address;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.Gb28181Sdp;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.ObjectUtils;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import java.net.InetAddress;

/**
 * SIP的工具类
 */
public class SipUtils {

    public static String getUserIdFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdToHeader(Request request) {
        ToHeader fromHeader = (ToHeader)request.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdFromHeader(ToHeader toHeader) {
        AddressImpl address = (AddressImpl)toHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }

    public static String getUserIdFromHeader(FromHeader fromHeader) {
        AddressImpl address = (AddressImpl)fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }

    public static  String getNewViaTag() {
        return "z9hG4bKPj" + getNewFromTag();
    }

    public static String getNewFromTag(){
        return RandomStringUtils.randomAlphanumeric(32);
    }

    public static String getNewTag(){
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 从请求中获取设备ip地址和端口号
     * @param request 请求
     * @param sipUseSourceIpAsRemoteAddress  false 从via中获取地址， true 直接获取远程地址
     * @return 地址信息
     */
    public static Address getRemoteAddressFromRequest(SIPRequest request, boolean sipUseSourceIpAsRemoteAddress) {
        String remoteAddress;
        int remotePort;
        if (sipUseSourceIpAsRemoteAddress) {
            remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
            remotePort = request.getPeerPacketSourcePort();
        }else {
            // 判断RPort是否改变，改变则说明路由nat信息变化，修改设备信息
            // 获取到通信地址等信息
            Via topmostViaHeader = (Via) request.getTopmostViaHeader();
            remoteAddress = topmostViaHeader.getReceived();
            remotePort = topmostViaHeader.getRPort();
            // 解析本地地址替代
            if (ObjectUtils.isEmpty(remoteAddress) || remotePort == -1) {
                InetAddress peerPacketSourceAddress = request.getPeerPacketSourceAddress();
                if(peerPacketSourceAddress ==null){
                    remoteAddress = request.getRemoteAddress().getHostAddress();
                    remotePort = request.getRemotePort();
                }else {
                    remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
                    remotePort = request.getPeerPacketSourcePort();
                }
            }
        }
        return Address.builder().ip(remoteAddress).port(remotePort).build();
    }

    public static String getSsrcFromSdp(String sdpStr) {

        // jainSip不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        if (ssrcIndex == 0) {
            return null;
        }
        String lines[] = sdpStr.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().startsWith("y=")) {
                return line.substring(2);
            }
        }
        return null;
    }

    public static Gb28181Sdp parseSDP(String sdpStr) throws SdpParseException {
        // jainSip不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        int mediaDescriptionIndex = sdpStr.indexOf("f=");
        // 检查是否有y字段
        SessionDescription sdp;
        String ssrc = null;
        String mediaDescription = null;
        if (mediaDescriptionIndex == 0 && ssrcIndex == 0) {
            sdp = SdpFactory.getInstance().createSessionDescription(sdpStr);
        }else {
            String lines[] = sdpStr.split("\\r?\\n");
            StringBuilder sdpBuffer = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("y=")) {
                    ssrc = line.substring(2);
                }else if (line.trim().startsWith("f=")) {
                    mediaDescription = line.substring(2);
                }else {
                    sdpBuffer.append(line.trim()).append("\r\n");
                }
            }
            sdp = SdpFactory.getInstance().createSessionDescription(sdpBuffer.toString());
        }
        return Gb28181Sdp.getInstance(sdp, ssrc, mediaDescription);
    }
}
