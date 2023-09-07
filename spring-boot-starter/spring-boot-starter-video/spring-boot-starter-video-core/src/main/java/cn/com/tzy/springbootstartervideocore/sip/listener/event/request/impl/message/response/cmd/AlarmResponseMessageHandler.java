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
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import java.util.Map;

/**
 * 报警回复信息
 */
@Log4j2
public class AlarmResponseMessageHandler extends SipResponseEvent implements MessageHandler {

    @Resource
    private DeferredResultHolder deferredResultHolder;

    public AlarmResponseMessageHandler(ResponseMessageHandler handler){
        handler.setMessageHandler(CmdType.ALARM_RESPONSE.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        String channelId = XmlUtils.getText(element,"DeviceID");
        String key = String.format("%s%s", DeferredResultHolder.CALLBACK_CMD_ALARM,deviceVo.getDeviceId());
        Map<String, Object> map = XmlUtil.xmlToMap(element);
        if (log.isDebugEnabled()) {
            log.debug(map);
        }
        deferredResultHolder.invokeAllResult(key, RestResult.result(RespCode.CODE_0.getValue(),null,map));
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        //没有上级回复
    }
}
