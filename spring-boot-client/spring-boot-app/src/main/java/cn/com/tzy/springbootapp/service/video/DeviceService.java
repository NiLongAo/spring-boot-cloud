package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootfeignvideo.api.video.DeviceServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备通道相关接口
 */
@Service
public class DeviceService {

    @Resource
    private DeviceServiceFeign deviceServiceFeign;


    /**
     * 分页
     */
    public PageResult page(PageModel param){
        return deviceServiceFeign.page(param);
    }


    /**
     * 根据国标设备编号获取设备
     */
    public RestResult<?> findDeviceId(String deviceId){
        return deviceServiceFeign.findDeviceId(deviceId);
    }

    /**
     * 移除设备
     */
    public RestResult<?> del(String deviceId){
        return deviceServiceFeign.del(deviceId);
    }

    /**
     * 修改数据流传输模式
     * @param deviceId 设备编号
     * @param streamMode 传输协议 1.UDP 2.TCP
     * @return
     */
    public RestResult<?> updateTransport(String deviceId,Integer streamMode){
        return deviceServiceFeign.updateTransport(deviceId,streamMode);
    }

    /**
     * 设备保存
     * @param param 设备信息
     * @return
     */
    public RestResult<?> saveDevice(Device param){
        return deviceServiceFeign.saveDevice(param);
    }

    /**
     * 设备状态查询
     */
    public RestResult<?> findDeviceStatus(String deviceId){
        return deviceServiceFeign.findDeviceStatus(deviceId);
    }

    /**
     * 设备报警查询
     * @param deviceId 设备id
     * @param startPriority 报警起始级别（可选）
     * @param endPriority 报警终止级别（可选）
     * @param alarmMethod 报警方式条件（可选）
     * @param alarmType 报警类型
     * @param startTime 报警发生起始时间（可选）
     * @param endTime 报警发生终止时间（可选）
     * @return
     */
    public RestResult<?> findDeviceAlarm(String deviceId,String startPriority,String endPriority,String alarmMethod,String alarmType,String startTime,String endTime){
        return deviceServiceFeign.findDeviceAlarm(deviceId,startPriority,endPriority,alarmMethod,alarmType,startTime,endTime);
    }

    /**
     * 获取设备的订阅状态
     * @param deviceId 设备id
     * @return
     */
    public RestResult<?> subscribeInfo(String deviceId){
        return deviceServiceFeign.subscribeInfo(deviceId);
    }


    /**
     * 基本配置设置命令
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param name 名称
     * @param expiration 到期时间
     * @param heartBeatInterval 心跳间隔
     * @param heartBeatCount 心跳计数
     * @return
     */
    public RestResult<?> basicParam(String deviceId,String channelId,String name,String expiration,String heartBeatInterval,String heartBeatCount){
        return deviceServiceFeign.basicParam(deviceId,channelId,name,expiration,heartBeatInterval,heartBeatCount);
    }

    /**
     * 设备配置查询请求
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param configType 配置类型 查询配置参数类型(必选),可查询的配置类型包括
     *                   基本参数配置:BasicParam,
     *                   视频参数范围:VideoParamOpt,
     *                   SVAC编码配置:SVACEncodeConfig
     *                   SVAC解码配置:SVACDecodeConfig
     * 应,每个响应对应一个配置类型。
     * @return
     */
    public RestResult<?> queryParam(String deviceId,String channelId,String configType){
        return deviceServiceFeign.queryParam(deviceId,channelId,configType);
    }

    /**
     * 远程启动控制
     */
    public RestResult<?> startControl(String deviceId){
        return deviceServiceFeign.startControl(deviceId);
    }

    /**
     * 录像控制命令
     * @param deviceId 设备id
     * @param channelId 通道编号
     * @param status 操作状态 0.停止 1.启用
     * @return
     */
    public RestResult<?> recordControl(String deviceId, String channelId,Integer status){
        return deviceServiceFeign.recordControl(deviceId,channelId,status);
    }

    /**
     * 布防/撤防命令
     * @param deviceId 设备id
     * @param status 操作状态 0.撤防 1.布防
     * @return
     */
    public RestResult<?> guardControl(String deviceId,String channelId,Integer status){
        return deviceServiceFeign.guardControl(deviceId,channelId,status);
    }

    /**
     * 报警复位
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param alarmMethod 报警方式
     * @param alarmType 报警类型
     * @return
     */
    public RestResult<?> resetAlarm(String deviceId, String channelId, String alarmMethod, String alarmType){
        return deviceServiceFeign.resetAlarm(deviceId,channelId,alarmMethod,alarmType);
    }

    /**
     * 强制关键帧
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @return
     */
    public RestResult<?> iFrame(String deviceId, String channelId){
        return deviceServiceFeign.iFrame(deviceId,channelId);
    }

    /**
     * 看守位控制
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param enabled 看守位使能1:开启,0:关闭
     * @param resetTime 自动归位时间间隔，开启看守位时使用，单位:秒(s)
     * @param presetIndex 调用预置位编号，开启看守位时使用，取值范围0~255
     * @return
     */
    public RestResult<?> homePosition(String deviceId,String channelId,Integer enabled,Integer resetTime,Integer presetIndex){
        return deviceServiceFeign.homePosition(deviceId,channelId,enabled,resetTime,presetIndex);
    }

    /**
     * 拉框放大
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param length 播放窗口长度像素值
     * @param width 播放窗口宽度像素值
     * @param midpointx 拉框中心的横轴坐标像素值
     * @param midpointy 拉框中心的纵轴坐标像素值
     * @param lengthx 拉框长度像素值
     * @param lengthy 拉框宽度像素值
     * @return
     */
    public RestResult<?> zoomIn(String deviceId,String channelId,Integer length,Integer width,Integer midpointx,Integer midpointy,Integer lengthx,Integer lengthy){
        return deviceServiceFeign.zoomIn(deviceId,channelId,length,width,midpointx,midpointy,lengthx,lengthy);
    }


    /**
     * 拉框缩小
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param length 播放窗口长度像素值
     * @param width 播放窗口宽度像素值
     * @param midpointx 拉框中心的横轴坐标像素值
     * @param midpointy 拉框中心的纵轴坐标像素值
     * @param lengthx 拉框长度像素值
     * @param lengthy 拉框宽度像素值
     * @return
     */
    public RestResult<?> zoomOut(String deviceId,String channelId,Integer length,Integer width,Integer midpointx,Integer midpointy,Integer lengthx,Integer lengthy){
        return deviceServiceFeign.zoomOut(deviceId,channelId,length,width,midpointx,midpointy,lengthx,lengthy);
    }
}
