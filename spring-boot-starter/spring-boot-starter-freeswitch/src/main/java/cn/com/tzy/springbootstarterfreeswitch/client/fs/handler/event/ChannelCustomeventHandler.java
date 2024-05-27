package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGroupInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 *  用户注册注销事件
 */
@Log4j2
@Component
@Order(30)
@EslEventName(EventNames.CUSTOM)
public class ChannelCustomeventHandler implements EslEventHandler {
    //开始缓存用户信息
    @Override
    public void handle(String addr, EslEvent event) {
        String userName = event.getEventHeaders().get("user_name");//主叫号码
        String eventSubclass = event.getEventHeaders().get("Event-Subclass");//相关事件
        if ("sofia::register".equals(eventSubclass)) {
            log.info("进入事件 [ 用户注册 ] CUSTOM");
            String networkIp = event.getEventHeaders().get("network-ip");
            String networkPort = event.getEventHeaders().get("network-port");
            //注册
            AgentVoInfo agentVoInfo = FsService.getAgentService().getAgentBySip(userName);
            if (agentVoInfo != null) {
                RedisService.getAgentInfoManager().put(agentVoInfo);
                FsService.getAgentService().online(agentVoInfo, null);
                CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(agentVoInfo.getCompanyId());
                if (companyInfo != null) {
                    RouteGroupInfo routeGroupInfo = companyInfo.getRouteGroupMap().computeIfAbsent(agentVoInfo.getAgentCode(), k -> new RouteGroupInfo());
                    routeGroupInfo.setRouteGateWayInfoList(Collections.singletonList(RouteGateWayInfo.builder()
                            .name(String.format("坐席AgentCode：%s", agentVoInfo.getAgentCode()))
                            .mediaHost(networkIp)
                            .profile("internal")
                            .mediaPort(Integer.valueOf(networkPort))
                            .build()));
                    RedisService.getCompanyInfoManager().put(companyInfo);
                }

            }
        }else if ("sofia::unregister".equals(eventSubclass)) {
            log.info("进入事件 [ 用户注销 ] CUSTOM");
            //注销
            AgentVoInfo agentBySip = FsService.getAgentService().getAgentBySip(userName);
            if (agentBySip != null) {
                CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(agentBySip.getCompanyId());
                if (companyInfo != null) {
                    companyInfo.getRouteGroupMap().remove(agentBySip.getAgentCode());
                    RedisService.getCompanyInfoManager().put(companyInfo);
                }
                FsService.getAgentService().offline(agentBySip.getAgentKey());
            }
        }
    }
}
