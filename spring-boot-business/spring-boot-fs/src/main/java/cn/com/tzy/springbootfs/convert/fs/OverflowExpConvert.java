package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.OverflowExp;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.OverflowExpInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OverflowExpConvert {
    OverflowExpConvert INSTANCE = Mappers.getMapper(OverflowExpConvert.class);
    OverflowExp convert(OverflowExpInfo param);

    List<OverflowExp> convertOverflowExpList(List<OverflowExpInfo> param);
    OverflowExpInfo convert(OverflowExp param);
    List<OverflowExpInfo> convertOverflowExpInfoList(List<OverflowExp> param);
}
