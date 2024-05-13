package cn.com.tzy.springbootfs.service.fs;

import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AgentService extends IService<Agent>{

    UserModel findUserModel(String agentCode);
}
