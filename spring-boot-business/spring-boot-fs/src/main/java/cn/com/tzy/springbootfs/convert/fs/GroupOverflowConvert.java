package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupOverflow;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupOverFlowInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupOverflowConvert {

    GroupOverflowConvert INSTANCE = Mappers.getMapper(GroupOverflowConvert.class);
    GroupOverflow convert(GroupOverFlowInfo param);

    List<GroupOverflow> convertGroupOverflowList(List<GroupOverFlowInfo> param);
    GroupOverFlowInfo convert(GroupOverflow param);
    List<GroupOverFlowInfo> convertGroupOverflowInfoList(List<GroupOverflow> param);
}
