package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.VdnDtmf;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnDtmfInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VdnDtmfConvert {

    VdnDtmfConvert INSTANCE = Mappers.getMapper(VdnDtmfConvert.class);
    VdnDtmf convert(VdnDtmfInfo param);

    List<VdnDtmf> convertVdnScheduleList(List<VdnDtmfInfo> param);
    VdnDtmfInfo convert(VdnDtmf param);
    List<VdnDtmfInfo> convertVdnScheduleInfoList(List<VdnDtmf> param);
}
