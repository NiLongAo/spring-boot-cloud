package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ParentPlatformMapper extends BaseMapper<ParentPlatform> {
    List<Map> findChannelSource(@Param("serverGbId") String serverGbId, @Param("channelId") String channelId);

    List<ParentPlatform> queryPlatFormListForGBWithGBId(@Param("channelId") String channelId, @Param("allPlatformId") List<String> allPlatformId);

    List<ParentPlatform> queryPlatFormListForStreamWithGBId(@Param("app") String app, @Param("stream") String stream, @Param("allPlatformId") List<String> allPlatformId);


}