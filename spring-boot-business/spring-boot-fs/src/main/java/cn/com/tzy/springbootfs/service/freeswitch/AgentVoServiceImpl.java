package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.convert.fs.AgentConvert;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AgentVoServiceImpl extends AgentVoService {

    @Resource
    private AgentMapper agentMapper;

    @Override
    public AgentVoInfo getAgentBySip(String sip) {
        Agent agent = agentMapper.selectOne(new LambdaQueryWrapper<Agent>().eq(Agent::getAgentCode,sip));
        return getAgentVoInfo(agent);
    }

    @Override
    public AgentVoInfo findAgentId(String id) {
        Agent agent = agentMapper.selectById(id);
        return getAgentVoInfo(agent);
    }

    @Override
    public void save(AgentVoInfo entity) {
        Agent agent = AgentConvert.INSTANCE.convert(entity);
        if(agent.getId() != null){
            agentMapper.updateById(agent);
        }else {
            agentMapper.insert(agent);
        }

    }

    @Override
    public void updateStatus(Long id, boolean b) {
        Agent agent = agentMapper.selectById(id);
        Agent build = Agent.builder()
                .id(agent.getId())
                .host("")
                .state(b ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue())
                .registerTime(b ?new Date():null)
                .renewTime(b ?new Date():null)
                .keepaliveTime(b ?new Date():null)
                .build();
        agentMapper.updateById(build);
    }

    @Override
    public void startPlay(String agentCode, String stream) {

    }

    @Override
    public void stopPlay(String agentCode) {

    }

    private AgentVoInfo getAgentVoInfo(Agent agent) {
        if(agent == null){
            return null;
        }
        AgentVoInfo convert = AgentConvert.INSTANCE.convert(agent);
        convert.setAgentOnline(ConstEnum.Flag.NO.getValue());
        convert.setSsrcCheck(ConstEnum.Flag.NO.getValue());
        return convert;
    }
}
