package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.cmd;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.ResponseMessageHandler;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import cn.hutool.core.util.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Map;

/**
 * 设备配置查询的回复
 */
@Log4j2
public class ConfigDownloadResponseMessageHandler extends SipResponseEvent implements MessageHandler {

    @Resource
    private DeferredResultHolder deferredResultHolder;

    public ConfigDownloadResponseMessageHandler(ResponseMessageHandler handler){
        handler.setMessageHandler(CmdType.CONFIG_DOWNLOAD_RESPONSE.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        String channelId = XmlUtils.getText(element, "DeviceID");
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_CONFIGDOWNLOAD,deviceVo.getDeviceId(),channelId);
        try {
            // 回复200 OK
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 设备配置查询: {}", e.getMessage());
        }
        // 此处是对本平台发出DeviceControl指令的应答
        Map<String, Object> map = XmlUtil.xmlToMap(element);
        if (log.isDebugEnabled()) {
            log.debug(map);
        }
        deferredResultHolder.invokeAllResult(key, RestResult.result(RespCode.CODE_0.getValue(),null,map));
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        // 不会收到上级平台的设备配置查询
    }
}
