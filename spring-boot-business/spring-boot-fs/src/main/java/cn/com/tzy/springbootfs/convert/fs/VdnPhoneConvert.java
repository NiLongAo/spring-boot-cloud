package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.VdnPhone;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnPhoneInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VdnPhoneConvert {
    VdnPhoneConvert INSTANCE = Mappers.getMapper(VdnPhoneConvert.class);
    VdnPhone convert(VdnPhoneInfo param);

    List<VdnPhone> convertVdnScheduleList(List<VdnPhoneInfo> param);
    VdnPhoneInfo convert(VdnPhone param);
    List<VdnPhoneInfo> convertVdnScheduleInfoList(List<VdnPhone> param);
}
