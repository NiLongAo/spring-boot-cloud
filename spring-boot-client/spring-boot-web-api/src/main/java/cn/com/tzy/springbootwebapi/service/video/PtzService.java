package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.PtzServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 云台控制
 */
@Service
public class PtzService {

    @Resource
    private PtzServiceFeign ptzServiceFeign;

    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param command 控制指令
     * @param horizonSpeed 水平移动速度
     * @param verticalSpeed 垂直移动速度
     * @param zoomSpeed 缩放速度
     * @return
     */
    public RestResult<?> ptz(String deviceId,String channelId,String command,Integer horizonSpeed,Integer verticalSpeed,Integer zoomSpeed){
        return ptzServiceFeign.ptz(deviceId,channelId,command,horizonSpeed,verticalSpeed,zoomSpeed);
    }

    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param cmdCode 控制指令
     * @param parameter1 水平移动速度
     * @param parameter2 垂直移动速度
     * @param combindCode2 缩放速度
     * @return
     */
    public RestResult<?> frontEndCommand(String deviceId,String channelId,Integer cmdCode,Integer parameter1,Integer parameter2,Integer combindCode2){
        return ptzServiceFeign.frontEndCommand(deviceId,channelId,cmdCode,parameter1,parameter2,combindCode2);
    }

    /**
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    public RestResult<?> presetQuery(String deviceId,String channelId){
        return ptzServiceFeign.presetQuery(deviceId,channelId);
    }



}
