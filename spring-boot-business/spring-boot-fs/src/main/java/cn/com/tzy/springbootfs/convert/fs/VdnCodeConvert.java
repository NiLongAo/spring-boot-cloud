package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.VdnCode;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnCodeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VdnCodeConvert {
    VdnCodeConvert INSTANCE = Mappers.getMapper(VdnCodeConvert.class);
    VdnCode convert(VdnCodeInfo param);

    List<VdnCode> convertVdnCodeList(List<VdnCodeInfo> param);
    VdnCodeInfo convert(VdnCode param);
    List<VdnCodeInfo> convertVdnCodeInfoList(List<VdnCode> param);
}
