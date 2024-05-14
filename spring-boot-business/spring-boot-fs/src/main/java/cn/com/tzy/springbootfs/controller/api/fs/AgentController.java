package cn.com.tzy.springbootfs.controller.api.fs;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@Log4j2
@RestController("ApiAgentController")
@RequestMapping(value = "/api/fs/agent")
public class AgentController extends ApiController {

    @Resource
    private AgentService agentService;

    /**
     * 获取拨号计划的xml信息
     */

    @PostMapping( "/login")
    public DeferredResult<RestResult<?>> login(@RequestBody Agent agent){
        return agentService.login(agent);
    }


}
