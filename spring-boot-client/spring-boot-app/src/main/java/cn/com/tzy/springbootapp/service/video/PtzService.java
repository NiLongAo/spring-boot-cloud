package cn.com.tzy.springbootapp.service.video;

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
    public RestResult<?> ptz(String deviceId, String channelId, String command, Integer horizonSpeed, Integer verticalSpeed, Integer zoomSpeed) {
        return ptzServiceFeign.ptz(deviceId, channelId, command, horizonSpeed, verticalSpeed, zoomSpeed);
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
    public RestResult<?> frontEndCommand(String deviceId, String channelId, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode2) {
        return ptzServiceFeign.frontEndCommand(deviceId, channelId, cmdCode, parameter1, parameter2, combindCode2);
    }
    /**
     * 光圈控制
     * @return
     */
    public RestResult<?> iris(String deviceId, String channelId, String command, Integer speed) {
        return ptzServiceFeign.iris(deviceId, channelId, command, speed);
    }
    /**
     * 聚焦控制
     * @return
     */
    public RestResult<?> focus(String deviceId, String channelId, String command, Integer speed) {
        return ptzServiceFeign.focus(deviceId, channelId, command, speed);
    }
    /**
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    public RestResult<?> presetQuery(String deviceId, String channelId) {
        return ptzServiceFeign.presetQuery(deviceId, channelId);
    }
    /**
     * 预置位指令-设置预置位
     * @return
     */
    public RestResult<?> addPreset(String deviceId, String channelId, Integer presetId) {
        return ptzServiceFeign.addPreset(deviceId, channelId, presetId);
    }
    /**
     * 预置位指令-调用预置位
     * @return
     */
    public RestResult<?> callPreset(String deviceId, String channelId, Integer presetId) {
        return ptzServiceFeign.callPreset(deviceId, channelId, presetId);
    }
    /**
     * 预置位指令-删除预置位
     * @return
     */
    public RestResult<?> delPreset(String deviceId, String channelId, Integer presetId) {
        return ptzServiceFeign.delPreset(deviceId, channelId, presetId);
    }
    /**
     * 巡航指令-加入巡航点
     * @return
     */
    public RestResult<?> addCruisePoint(String deviceId, String channelId, Integer presetId, Integer cruiseId) {
        return ptzServiceFeign.addCruisePoint(deviceId, channelId, presetId, cruiseId);
    }
    /**
     * 巡航指令-删除一个巡航点
     * @return
     */
    public RestResult<?> delCruisePoint(String deviceId, String channelId, Integer presetId, Integer cruiseId) {
        return ptzServiceFeign.delCruisePoint(deviceId, channelId, presetId, cruiseId);
    }
    /**
     * 巡航指令-设置巡航速度
     * @return
     */
    public RestResult<?> speedCruisePoint(String deviceId, String channelId, Integer cruiseId, Integer speed) {
        return ptzServiceFeign.speedCruisePoint(deviceId, channelId, cruiseId, speed);
    }
    /**
     * 巡航指令-设置巡航停留时间
     * @return
     */
    public RestResult<?> timeCruisePoint(String deviceId, String channelId, Integer cruiseId, Integer time) {
        return ptzServiceFeign.timeCruisePoint(deviceId, channelId, cruiseId, time);
    }
    /**
     * 巡航指令-开始巡航
     * @return
     */
    public RestResult<?> startCruisePoint(String deviceId, String channelId, Integer cruiseId) {
        return ptzServiceFeign.startCruisePoint(deviceId, channelId, cruiseId);
    }
    /**
     * 巡航指令-停止巡航
     * @return
     */
    public RestResult<?> stopCruisePoint(String deviceId, String channelId, Integer cruiseId) {
        return ptzServiceFeign.stopCruisePoint(deviceId, channelId, cruiseId);
    }
    /**
     * 扫描指令-开始自动扫描
     * @return
     */
    public RestResult<?> startScan(String deviceId, String channelId, Integer scanId) {
        return ptzServiceFeign.startScan(deviceId, channelId, scanId);
    }
    /**
     * 扫描指令-停止自动扫描
     * @return
     */
    public RestResult<?> stopScan(String deviceId, String channelId, Integer scanId) {
        return ptzServiceFeign.stopScan(deviceId, channelId, scanId);
    }

    /**
     * 扫描指令-设置自动扫描左边界
     * @return
     */
    public RestResult<?> setLeftScan(String deviceId, String channelId, Integer scanId) {
        return ptzServiceFeign.setLeftScan(deviceId, channelId, scanId);
    }
    /**
     * 扫描指令-设置自动扫描右边界
     * @return
     */
    public RestResult<?> setRightScan(String deviceId, String channelId, Integer scanId) {
        return ptzServiceFeign.setRightScan(deviceId, channelId, scanId);
    }
    /**
     * 扫描指令-设置自动扫描速度
     * @return
     */
    public RestResult<?> setSpeedScan(String deviceId, String channelId, Integer scanId, Integer speed) {
        return ptzServiceFeign.setSpeedScan(deviceId, channelId, scanId, speed);
    }
    /**
     * 辅助开关控制指令-雨刷控制
     * @return
     */
    public RestResult<?> wiper(String deviceId, String channelId, String command) {
        return ptzServiceFeign.wiper(deviceId, channelId, command);
    }
    /**
     * 辅助开关控制指令-雨刷控制
     * @return
     */
    public RestResult<?> auxiliary(String deviceId, String channelId, String command, Integer switchId) {
        return ptzServiceFeign.auxiliary(deviceId, channelId, command, switchId);
    }
}
