package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.DeviceVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.ParentPlatformVo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPResponseProvider;
import cn.hutool.core.util.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Request;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public abstract class SipResponseEvent {

    @Resource
    protected SipServer sipServer;
    @Resource
    protected SipMessageHandle sipMessageHandle;
    @Resource
    protected VideoProperties videoProperties;
    @Resource
    protected SIPCommander sipCommander;
    @Resource
    protected SIPCommanderForPlatform sipCommanderForPlatform;


    /**
     * 信息回复
     */
    public void responseAck(SIPRequest request, int statusCode, String msg) throws InvalidArgumentException, SipException, ParseException {
        SIPResponse sipResponse = SIPResponseProvider.builder(sipServer, statusCode, msg, null, request)
                .buildResponse();
        sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),sipResponse);
    }

    /**
     * 回复带sdp
     */
    public SIPResponse responseSdpAck(SIPRequest request, String sdp, ParentPlatformVo parentPlatformVo) throws InvalidArgumentException, SipException, ParseException {
        SIPResponse sipResponse = SIPResponseProvider.builder(sipServer, 200, null, sdp, request)
                .createSipURI(parentPlatformVo.getServerGbId(),String.format("%s:%s", parentPlatformVo.getServerIp(), parentPlatformVo.getServerPort()))
                .createContentTypeHeader("APPLICATION","SDP")
                .createContactHeader()
                .buildResponse();
        sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),sipResponse);
        return sipResponse;
    }

    /**
     * 回复带sdp
     */
    public SIPResponse responseSdpAck(SIPRequest request, String sdp, DeviceVo deviceVo) throws InvalidArgumentException, SipException, ParseException {
        SIPResponse sipResponse = SIPResponseProvider.builder(sipServer, 200, null, sdp, request)
                .createSipURI(deviceVo.getDeviceId(),deviceVo.getHostAddress())
                .createContentTypeHeader("APPLICATION","SDP")
                .createContactHeader()
                .buildResponse();
        sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),sipResponse);
        return sipResponse;
    }

    /**
     * 回复带sdp
     */
    public SIPResponse responseXmlAck(SIPRequest request, String xml, ParentPlatformVo parentPlatformVo, Integer expires) throws InvalidArgumentException, SipException, ParseException {
        SIPResponse sipResponse = SIPResponseProvider.builder(sipServer, 200, null, xml, request)
                .createSipURI(parentPlatformVo.getServerGbId(),String.format("%s:%s", parentPlatformVo.getServerIp(), parentPlatformVo.getServerPort()))
                .createExpiresHeader(expires)
                .createContentTypeHeader("APPLICATION","SDP")
                .createContactHeader()
                .buildResponse();
        sipMessageHandle.handleMessage(request.getLocalAddress().getHostAddress(),sipResponse);
        return sipResponse;
    }


    public Element getRootElement(RequestEvent evt){
        return getRootElement(evt, "gb2312");
    }

    public Element getRootElement(RequestEvent evt, String charset) {
        if (charset == null) {
            charset = "gb2312";
        }
        Request request = evt.getRequest();
        // 对海康出现的未转义字符做处理。
        String[] destStrArray = new String[]{"&lt;","&gt;","&amp;","&apos;","&quot;"};
        char despChar = '&'; // 或许可扩展兼容其他字符
        byte destBye = (byte) despChar;
        List<Byte> result = new ArrayList<>();
        byte[] rawContent = request.getRawContent();
        if (rawContent == null) {
            return null;
        }
        for (int i = 0; i < rawContent.length; i++) {
            if (rawContent[i] == destBye) {
                boolean resul = false;
                for (String destStr : destStrArray) {
                    if (i + destStr.length() <= rawContent.length) {
                        byte[] bytes = Arrays.copyOfRange(rawContent, i, i + destStr.length());
                        resul = resul || (Arrays.equals(bytes,destStr.getBytes()));
                    }
                }
                if (resul) {
                    result.add(rawContent[i]);
                }
            }else {
                result.add(rawContent[i]);
            }
        }
        Byte[] bytes = new Byte[0];
        byte[] bytesResult = ArrayUtils.toPrimitive(result.toArray(bytes));
        String context = new String(bytesResult, Charset.forName(charset)).trim();
        Document document = XmlUtil.parseXml(context);
        return XmlUtil.getRootElement(document);
    }
}
