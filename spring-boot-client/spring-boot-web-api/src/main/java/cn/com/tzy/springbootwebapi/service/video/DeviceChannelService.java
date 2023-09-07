package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import cn.com.tzy.springbootfeignvideo.api.video.DeviceChannelServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备通道相关接口
 */
@Service
public class DeviceChannelService {

    @Resource
    private DeviceChannelServiceFeign deviceChannelServiceFeign;

    /**
     * 获取设备通道树
     */
    public RestResult<?> tree(){
        return deviceChannelServiceFeign.tree();
    }
    /**
     * 通道分页
     */
    public PageResult page(DeviceChannelPageParam param){
        return deviceChannelServiceFeign.page(param);
    }

    /**
     * 通道分页
     */
    public RestResult<?> detail( String channelId){
        return deviceChannelServiceFeign.detail(channelId);
    }

    /**
     * 保存通道信息
     */
    public RestResult<?> save(DeviceChannel param){
        return deviceChannelServiceFeign.save(param);
    }

    /**
     * 同步设备通道
     */
    public RestResult<?> sync(String deviceId){
        return deviceChannelServiceFeign.sync(deviceId);
    }

    /**
     * 获取通道同步进度
     */
    public RestResult<?> syncStatus(String deviceId){
        return deviceChannelServiceFeign.syncStatus(deviceId);
    }
    /**
     * 删除通道
     */
    public RestResult<?> del(String deviceId, String channelId){
        return deviceChannelServiceFeign.del(deviceId,channelId);
    }

}
