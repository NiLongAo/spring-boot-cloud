package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.GbVideoServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 国标流相关接口
 */
@Service
public class GbVideoService {
    
    @Resource
    private GbVideoServiceFeign gbVideoServiceFeign;

    /**
     * 录像查询列表
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public RestResult<?> list(String deviceGbId, String channelId, String startTime, String endTime){
        return gbVideoServiceFeign.list(deviceGbId,channelId,startTime,endTime);
    }

    /**
     * 开始下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param downloadSpeed 下载monitorIp
     * @return
     */
    public RestResult<?> start(String deviceGbId, String channelId, String startTime, String endTime, Integer downloadSpeed){
        return gbVideoServiceFeign.start(deviceGbId,channelId,startTime,endTime,downloadSpeed);
    }


    /**
     * 停止下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param stream 流ID
     * @return
     */
    public RestResult<?> stop(String deviceGbId, String channelId,String stream){
        return gbVideoServiceFeign.stop(deviceGbId,channelId,stream);
    }

    /**
     * 获取当前用户下载录像信息
     */
    public RestResult<?> list(){
        return gbVideoServiceFeign.list();
    }

    /**
     * 清除用户下载录像
     */
    public RestResult<?> del(String key){
        return gbVideoServiceFeign.del(key);
    }


}
