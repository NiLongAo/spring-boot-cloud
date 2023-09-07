package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;

import java.util.List;

public interface MediaServerVoService {

    MediaServerVo findMediaServerId(String mediaServerId);
    MediaServerVo findOnLineMediaServerId(String mediaServerId);
    List<MediaServerVo> findConnectZlmList();
    /**
     * 获取设备的流媒体信息或负载最低的服务
     * @return
     */
    MediaServerVo findMediaServerForMinimumLoad(DeviceVo deviceVo);
    /**
     * 获取流媒体负载最低的服务
     * @return
     */
    MediaServerVo findMediaServerForMinimumLoad();


    int  updateById(MediaServerVo mediaServerVo);

    int updateStatus(String mediaServerId,Integer status);
}
