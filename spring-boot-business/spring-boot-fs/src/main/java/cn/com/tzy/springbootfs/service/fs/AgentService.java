package cn.com.tzy.springbootfs.service.fs;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.context.request.async.DeferredResult;

public interface AgentService extends IService<Agent>{

    UserModel findUserModel(String agentCode);

    DeferredResult<RestResult<?>> login(Agent agent);

}
