package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootentity.dome.video.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    Device findPlatformIdChannelId(@Param("platformId") String platformId, @Param("channelId") String channelId);

    Device findDeviceInfoPlatformIdChannelId(@Param("platformId") String platformId, @Param("channelId") String channelId);

    Page<Device> findPage(Page<Device> page, @Param("param") PageModel param, @Param("administrator") Integer administrator);
}