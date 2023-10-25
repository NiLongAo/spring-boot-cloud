package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeviceChannelService extends IService<DeviceChannel>{

    DeviceChannel findPlatformIdChannelId( String platformId, String channelId);

    List<DeviceChannel> queryChannelWithCatalog(String serverGbId);

    List<DeviceChannel> queryGbStreamListInPlatform(String serverGbId,String gbId, boolean usPushingAsStatus);

    /**
     * 修改当前通道下子设备数量
     * @param deviceId
     * @param channelId
     */
    void updateChannelSubCount(String deviceId, String channelId);

    PageResult findPage(DeviceChannelPageParam param) throws Exception;
    RestResult<?> saveDeviceChannel(DeviceChannel param);
    RestResult<?> sync(String deviceId);
    RestResult<?> syncStatus(String deviceId);

    RestResult<?> findTreeDeviceChannel(boolean administrator) throws Exception;

    RestResult<?> detail(String channelId);

    RestResult<?> del(String deviceId, String channelId);


}
