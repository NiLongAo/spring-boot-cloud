package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.Group;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupConvert {
    GroupConvert INSTANCE = Mappers.getMapper(GroupConvert.class);
    Group convert(GroupInfo param);

    List<Group> convertGroupList(List<GroupInfo> param);
    GroupInfo convert(Group param);
    List<GroupInfo> convertGroupInfoList(List<Group> param);
}
