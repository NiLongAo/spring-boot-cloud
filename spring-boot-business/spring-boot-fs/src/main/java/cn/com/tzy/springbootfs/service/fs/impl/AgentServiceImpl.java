package cn.com.tzy.springbootfs.service.fs.impl;

import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService{
    @Override
    public UserModel findUserModel(String agentCode) {
        return baseMapper.findUserModel(agentCode);
    }
}
