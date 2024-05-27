package cn.com.tzy.springbootfs.service.fs;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.callback.InviteErrorCallback;
import cn.com.tzy.springbootstarterfreeswitch.common.interfaces.ResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SSRCInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AgentService extends IService<Agent>{
    //根据用户编号获取客服信息
    Agent findUserId(Long userId);
    UserModel findUserModel(String agentCode);
    RestResult<?> pushPath(String agentCode, Integer status);
    void login(String agentKey,ResultEvent event);
    RestResult<?> logout(String agentCode);
    public SSRCInfo callPhone(SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentBySip,String caller, String ssrc, InviteErrorCallback<Object> callback);

}
