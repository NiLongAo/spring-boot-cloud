package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.DeviceMobilePositionServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备通道相关接口
 */
@Service
public class DeviceMobilePositionService {

    @Resource
    private DeviceMobilePositionServiceFeign deviceMobilePositionServiceFeign;

    /**
     * 查询历史轨迹
     * @param deviceId 设备ID
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
   public RestResult<?> history(String deviceId,String channelId,String start,String end){
       return deviceMobilePositionServiceFeign.history(deviceId,channelId,start,end);
   }


    /**
     * 查询设备最新位置
     * @param deviceId 设备ID
     * @return
     */
    public RestResult<?> latest(String deviceId){
        return deviceMobilePositionServiceFeign.latest(deviceId);
    }

    /**
     * 获取移动位置信息
     * @param deviceId 设备ID
     * @return
     */
    public RestResult<?> realtime(String deviceId,String channelId){
        return deviceMobilePositionServiceFeign.realtime(deviceId,channelId);
    }

    /**
     * 订阅位置信息
     * @param deviceId 设备ID
     * @param expires 目录订阅周期
     * @param interval 位置订阅周期
     */
    public RestResult<?> subscribe(String deviceId,Integer expires,Integer interval){
        return deviceMobilePositionServiceFeign.subscribe(deviceId,expires,interval);
    }


    /**
     * 数据位置信息格式处理
     * @param deviceId 设备ID
     * @return
     */
    public RestResult<?> transform(String deviceId){
        return deviceMobilePositionServiceFeign.transform(deviceId);
    }

}
