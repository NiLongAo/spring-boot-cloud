package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootstartervideobasic.vo.video.PlatformCatalogVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlatformCatalogConvert {

    PlatformCatalogConvert INSTANCE = Mappers.getMapper(PlatformCatalogConvert.class);


    PlatformCatalogVo convert(PlatformCatalog param);

    List<PlatformCatalogVo> convertVo(List<PlatformCatalog> param);

    PlatformCatalog convert(PlatformCatalogVo param);

    List<PlatformCatalog> convertBean(List<PlatformCatalogVo> param);
}
