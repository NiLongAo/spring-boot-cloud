package cn.com.tzy.springbootfs.service.fs;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.callback.InviteErrorCallback;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SSRCInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.context.request.async.DeferredResult;

public interface AgentService extends IService<Agent>{

    UserModel findUserModel(String agentCode);
    RestResult<?> pushPath(String agentCode, Integer status);
    DeferredResult<RestResult<?>> login(Agent agent);
    public SSRCInfo callPhone(SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentBySip, String ssrc, InviteErrorCallback<Object> callback);


}
