package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.dome.video.PlatformGbStream;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlatformGbStreamMapper extends BaseMapper<PlatformGbStream> {

    List<GbStream> findGbStreamsList(
            @Param("isOn") int isOn,
            @Param("platformId") String platformId,
            @Param("catalogIdList") List<String> catalogIdList,
            @Param("gbIdList") List<String> gbIdList,
            @Param("query") String query
    );

    void delPlatformGbStream(@Param("app") String app, @Param("stream") String stream);
}