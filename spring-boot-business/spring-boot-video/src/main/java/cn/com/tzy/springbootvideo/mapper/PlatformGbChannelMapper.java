package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlatformGbChannelMapper extends BaseMapper<PlatformGbChannel> {
    List<DeviceChannel> findDeviceChannelList(
            @Param("isOn") int isOn,
            @Param("administrator") Integer administrator,
            @Param("online") Integer online,
            @Param("platformId") String platformId,
            @Param("catalogIdList") List<String> catalogIdList,
            @Param("gbIdList") List<String> gbIdList,
            @Param("query") String query
    );

    void delPlatformGbChannel(@Param("deviceId") String deviceId, @Param("channelId") String channelId);

    List<ParentPlatform> findChannelIdList(@Param("channelId") String channelId);
}