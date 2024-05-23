package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.SkillGroup;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.SkillGroupInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SkillGroupConvert {

    SkillGroupConvert INSTANCE = Mappers.getMapper(SkillGroupConvert.class);
    SkillGroup convert(SkillGroupInfo param);

    List<SkillGroup> convertSkillGroupList(List<SkillGroupInfo> param);
    SkillGroupInfo convert(SkillGroup param);
    List<SkillGroupInfo> convertSkillGroupInfoList(List<SkillGroup> param);
}
