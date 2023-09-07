package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlatformCatalogMapper extends BaseMapper<PlatformCatalog> {
    PlatformCatalog findId(@Param("id") String id);

    List<DeviceChannel> queryCatalogInPlatform(@Param("serverGbId") String serverGbId);

    List<PlatformCatalog> findGbStream(@Param("id") String id, @Param("platformId") String platformId);

    List<PlatformCatalog> findGbChannel(@Param("id") String id, @Param("platformId") String platformId);
}