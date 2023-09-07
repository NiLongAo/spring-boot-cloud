package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;

import java.util.List;

public interface DeviceChannelVoService {
    DeviceChannelVo findChannelId(String channelId);

    DeviceChannelVo findPlatformIdChannelId(String platformId, String channelId);

    DeviceChannelVo findDeviceIdChannelId(String deviceId, String channelId);

    void deviceChannelOnline(String deviceId,String channelId,boolean online);

    /**
     * 使用设备id与通道查询保存
     * @param deviceChannelVo
     * @return
     */
    int save(DeviceChannelVo deviceChannelVo);

    /**
     * 保存设备通道位置信息
     * @param deviceChannelVo
     * @return
     */
    void updateMobilePosition(DeviceChannelVo deviceChannelVo);

    int delAll(String deviceId);
    int del(String deviceId,String channelId);


    List<DeviceChannelVo> queryAllChannels(String deviceId);

    /**
     * 更新设备全部通道信息
     * 没有的新增 有的设备修改 对方没有我们有则删除 业务逻辑
     * @param deviceId 设备信息
     * @param channelList 通道信息集合
     */
    boolean resetChannels(String deviceId, List<DeviceChannelVo> channelList);
    // 目录与设备通道的关系
    List<DeviceChannelVo> queryChannelWithCatalog(String serverGbId);

    /**
     * 开始播放
     * @param deviceId
     * @param channelId
     */
    void startPlay(String deviceId, String channelId, String stream);

    /**
     * 停止播放
     * @param deviceId
     * @param channelId
     */
    void stopPlay(String deviceId, String channelId);

    /**
     * 获取语音对讲推流地址
     */
    RestResult<?> findAudioPushPath(String deviceId,String channelId);

    /**
     * 获取语音对讲推流状态
     */
    RestResult<?> findAudioPushStatus(String deviceId, String channelId);

    /**
     * 关闭语音推流
     * @return
     */
    RestResult<?> stopAudioPushStatus(String deviceId, String channelId);
}
