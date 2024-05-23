package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.VdnSchedule;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnScheduleInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VdnScheduleConvert {

    VdnScheduleConvert INSTANCE = Mappers.getMapper(VdnScheduleConvert.class);
    VdnSchedule convert(VdnScheduleInfo param);

    List<VdnSchedule> convertVdnScheduleList(List<VdnScheduleInfo> param);
    VdnScheduleInfo convert(VdnSchedule param);
    List<VdnScheduleInfo> convertVdnScheduleInfoList(List<VdnSchedule> param);
}
