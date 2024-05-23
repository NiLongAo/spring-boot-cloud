package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupStrategyExp;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupStrategyExpInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupStrategyExpConvert {
    GroupStrategyExpConvert INSTANCE = Mappers.getMapper(GroupStrategyExpConvert.class);
    GroupStrategyExp convert(GroupStrategyExpInfo param);

    List<GroupStrategyExp> convertGroupStrategyExpList(List<GroupStrategyExpInfo> param);
    GroupStrategyExpInfo convert(GroupStrategyExp param);
    List<GroupStrategyExpInfo> convertGroupStrategyExpInfoList(List<GroupStrategyExp> param);
}
