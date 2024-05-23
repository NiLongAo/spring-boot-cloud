package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.VdnConfig;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnConfigInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VdnConfigConvert {
    VdnConfigConvert INSTANCE = Mappers.getMapper(VdnConfigConvert.class);
    VdnConfig convert(VdnConfigInfo param);

    List<VdnConfig> convertVdnScheduleList(List<VdnConfigInfo> param);
    VdnConfigInfo convert(VdnConfig param);
    List<VdnConfigInfo> convertVdnScheduleInfoList(List<VdnConfig> param);
}
