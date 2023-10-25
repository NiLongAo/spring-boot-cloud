package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.vo.video.DeviceChannelTreeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceChannelMapper extends BaseMapper<DeviceChannel> {
    DeviceChannel findPlatformIdChannelId(@Param("platformId") String platformId, @Param("channelId") String channelId);

    List<DeviceChannel> queryChannelWithCatalog(@Param("serverGbId") String serverGbId);

    List<DeviceChannel> queryGbStreamListInPlatform(@Param("serverGbId") String serverGbId,@Param("gbId") String gbId, @Param("usPushingAsStatus") boolean usPushingAsStatus);

    void updateChannelSubCount(@Param("deviceId") String deviceId, @Param("channelId") String channelId);

    List<DeviceChannel> businessGroupList(@Param("deviceId") String deviceId, @Param("online") Integer online, @Param("isCivilCode") Boolean isCivilCode);

    List<DeviceChannelTreeVo> findTreeDeviceChannel(@Param("administrator") Integer administrator);
}