package cn.com.tzy.springbootfs.service.fs.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.result.DeferredResultHolder;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.FsRestResult;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService{

    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private AgentVoService agentVoService;

    @Override
    public UserModel findUserModel(String agentCode) {
        return baseMapper.findUserModel(agentCode);
    }

    @Override
    public DeferredResult<RestResult<?>> login(Agent entity) {
        String key = String.format("%s%s", DeferredResultHolder.AGENT_LOGIN,entity.getAgentCode());
        String uuid = RandomUtil.randomString(32);
        FsRestResult<RestResult<?>> result = new FsRestResult<>(8000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        if(deferredResultHolder.exist(key,null)){
            return result;
        }
        deferredResultHolder.put(key,uuid,result);

        AgentVoInfo agentVoInfo = agentVoService.getAgentBySip(entity.getAgentCode());
        if(agentVoInfo == null){
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"客服账号错误"));
            return result;
        }else if(!ObjectUtil.equals(agentVoInfo.getPasswd(),entity.getPasswd())){
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"客服密码错误"));
            return result;
        }
        SipService.getParentPlatformService().login(agentVoInfo,ok->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_0.getValue(),"登陆成功"));
        },error->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
        });
        return result;
    }
}
