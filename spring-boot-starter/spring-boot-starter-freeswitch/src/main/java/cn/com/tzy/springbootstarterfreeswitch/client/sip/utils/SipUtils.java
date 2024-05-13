package cn.com.tzy.springbootstarterfreeswitch.client.sip.utils;

import cn.com.tzy.springbootstarterfreeswitch.vo.sip.Address;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.ObjectUtils;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.header.FromHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.Request;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SIP的工具类
 */
public class SipUtils {

    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromFromHeader(fromHeader);
    }

    public static String getUserIdFromFromHeader(FromHeader fromHeader) {
        AddressImpl address = (AddressImpl)fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }

    public static  String getNewViaTag() {
        return "hs5G2sG" + RandomStringUtils.randomNumeric(10);
    }

    public static UserAgentHeader createUserAgentHeader(SipFactory sipFactory) throws PeerUnavailableException, ParseException {
        List<String> agentParam = new ArrayList<>();
        agentParam.add("Video-Zim");
        return sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
    }

    public static String getNewFromTag(){
        return UUID.randomUUID().toString().replace("-", "");
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
}
