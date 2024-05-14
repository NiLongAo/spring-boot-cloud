package cn.com.tzy.springbootfs.convert.fs;


import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AgentConvert {
    AgentConvert INSTANCE = Mappers.getMapper(AgentConvert.class);
    Agent convert(AgentVoInfo param);
    AgentVoInfo convert(Agent param);
}
