package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.impl;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.AbstractSipResponseEvent;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.message.Request;

/**
 * @description: BYE请求响应器
 */
@Log4j2
@Component
public class ByeResponseProcessor extends AbstractSipResponseEvent {

    @Override
    public String getMethod() {
        return Request.BYE;
    }

    @Override
    public void process(ResponseEvent event) {
        // TODO Auto-generated method stub
        log.info("BYE请求响应器 值:{}", JSONUtil.toJsonPrettyStr(event));
        //回复200
    }
}
