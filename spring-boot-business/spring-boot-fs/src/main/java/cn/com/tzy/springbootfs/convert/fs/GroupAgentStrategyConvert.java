package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupAgentStrategy;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupAgentStrategyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupAgentStrategyConvert {
    GroupAgentStrategyConvert INSTANCE = Mappers.getMapper(GroupAgentStrategyConvert.class);
    GroupAgentStrategy convert(GroupAgentStrategyInfo param);

    List<GroupAgentStrategy> convertCompanyList(List<GroupAgentStrategyInfo> param);
    GroupAgentStrategyInfo convert(GroupAgentStrategy param);
    List<GroupAgentStrategyInfo> convertCompanyInfoList(List<GroupAgentStrategy> param);
}
