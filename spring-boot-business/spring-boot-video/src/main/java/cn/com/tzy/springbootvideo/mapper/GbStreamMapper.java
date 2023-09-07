package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.dome.video.PlatformGbStream;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GbStreamMapper extends BaseMapper<GbStream> {
    GbStream findPlatformId(@Param("platformId") String platformId, @Param("gbId") String gbId);

    Page<GbStream> findPage(Page<GbStream> page, @Param("param") GbStreamPageParam param);
}