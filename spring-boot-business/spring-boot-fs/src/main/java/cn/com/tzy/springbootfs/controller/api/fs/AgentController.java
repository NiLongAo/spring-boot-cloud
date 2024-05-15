package cn.com.tzy.springbootfs.controller.api.fs;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.result.DeferredResultHolder;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.FsRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@Log4j2
@RestController("ApiAgentController")
@RequestMapping(value = "/api/fs/agent")
public class AgentController extends ApiController {

    @Resource
    private AgentService agentService;
    @Resource
    private SipServer sipServer;
    @Resource
    private DeferredResultHolder deferredResultHolder;

    /**
     * 获取拨号计划的xml信息
     */
    @PostMapping( "/login")
    public DeferredResult<RestResult<?>> login(@RequestBody Agent agent){
        return agentService.login(agent);
    }


    /**
     * 获取推流地址
     * @param status 1.音频 2.视频
     */
    @GetMapping("push_path")
    public RestResult<?> pushPath(@RequestParam("agentCode") String agentCode,@RequestParam("deviceId") Integer status) {
        return agentService.pushPath(agentCode,status);
    }

    /**
     * 拨打电话
     */
    @PostMapping( "/call_phone")
    public DeferredResult<RestResult<?>> callPhone(@RequestBody Agent agent){
        log.debug(String.format("开始拨打电话API调用，agnetCode：%s ", agent.getAgentCode()));
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s", DeferredResultHolder.CALL_PHONE,agent.getAgentCode());
        FsRestResult<RestResult<?>> result = new FsRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        if(deferredResultHolder.exist(key,null)){
            return result;
        }
        deferredResultHolder.put(key,uuid,result);
        AgentVoInfo agentBySip = FsService.getAgentService().getAgentBySip(agent.getAgentCode());
        if (agentBySip == null) {
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",agentBySip)));
            return result;
        }
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentBySip);
        if (mediaServerVo == null) {
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"流媒体未找到"));
            return result;
        }
        agentService.callPhone(sipServer,mediaServerVo,agentBySip,null,(code,msg,data)->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(code,msg,data));
        });
        return result;
    }


}
