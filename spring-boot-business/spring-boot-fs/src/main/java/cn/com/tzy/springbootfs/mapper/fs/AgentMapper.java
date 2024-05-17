package cn.com.tzy.springbootfs.mapper.fs;

import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentMapper extends BaseMapper<Agent> {
    UserModel findUserModel(@Param("agentCode") String agentCode);

    Agent findUserId(@Param("userId") Long userId);
}