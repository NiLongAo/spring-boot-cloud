package cn.com.tzy.springbootstartervideocore.sip.listener.event.response.impl;

import cn.com.tzy.springbootstartervideocore.sip.listener.event.response.AbstractSipResponseEvent;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;

import javax.sip.ResponseEvent;
import javax.sip.message.Request;

/**
 * @description: BYE请求响应器
 */
@Log4j2
public class ByeResponseProcessor extends AbstractSipResponseEvent {

    @Override
    public String getMethod() {
        return Request.BYE;
    }

    @Override
    public void process(ResponseEvent event) {
        // TODO Auto-generated method stub
        log.info("BYE请求响应器 值:{}", JSONUtil.toJsonPrettyStr(event));
    }
}
