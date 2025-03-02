package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootfeignvideo.api.video.MediaServerServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 国标流相关接口
 */
@Service
public class MediaServerService {

    @Resource
    private MediaServerServiceFeign mediaServerServiceFeign;

    /**
     * 分页
     */
    public PageResult page(MediaServerPageParam param){
        return mediaServerServiceFeign.page(param);
    }

    /**
     * 详情
     */
    public RestResult<?> detail(String id){
        return mediaServerServiceFeign.detail(id);
    }

    /**
     * 保存
     */
    public RestResult<?> save(MediaServerSaveParam param){
        return mediaServerServiceFeign.save(param);
    }


    /**
     * 移除
     */
    public RestResult<?> remove(String id){
        return mediaServerServiceFeign.remove(id);
    }

    /**
     * 根据应用名和流id获取播放地址
     * @param deviceId 设备编号
     * @param channelId 通道编号
     */
    public RestResult<?> findPlayUrl(String deviceId,String channelId){
        return mediaServerServiceFeign.findPlayUrl(deviceId,channelId);
    }

    /**
     * 获取流信息
     * @param app APP编号
     * @param stream 流编号
     * @param mediaServerId 流媒体信息
     */
    public RestResult<?> findMediaInfo(String app,String stream,String mediaServerId){
        return mediaServerServiceFeign.findMediaInfo(app,stream,mediaServerId);
    }
}
