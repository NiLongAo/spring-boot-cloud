package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupMemoryConfig;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupMemoryConfigInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupMemoryConfigConvert {

    GroupMemoryConfigConvert INSTANCE = Mappers.getMapper(GroupMemoryConfigConvert.class);
    GroupMemoryConfig convert(GroupMemoryConfigInfo param);

    List<GroupMemoryConfig> convertGroupMemoryConfigList(List<GroupMemoryConfigInfo> param);
    GroupMemoryConfigInfo convert(GroupMemoryConfig param);
    List<GroupMemoryConfigInfo> convertGroupMemoryConfigInfoList(List<GroupMemoryConfig> param);
}
