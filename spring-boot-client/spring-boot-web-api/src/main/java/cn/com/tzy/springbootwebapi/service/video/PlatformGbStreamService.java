package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootfeignvideo.api.video.PlatformGbStreamServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频流关联到级联平台
 */
@Service
public class PlatformGbStreamService {

    @Resource
    private PlatformGbStreamServiceFeign platformGbStreamServiceFeign;

    /**
     * 级联视频流列表
     */
    public RestResult<?> findGbStreamList(){
        return platformGbStreamServiceFeign.findGbStreamList();
    }

    /**
     * 级联视频流关联列表
     */
    public RestResult<?> findStreamBindKey(PlatformGbStreamParam param){
        return platformGbStreamServiceFeign.findStreamBindKey(param);
    }
    /**
     * 添加关联平台国标流信息
     */
    public RestResult<?> add(PlatformGbStreamSaveParam param){
        return platformGbStreamServiceFeign.add(param);
    }


    /**
     * 移除关联平台国标流信息
     */
    public RestResult<?> del(PlatformGbStreamSaveParam param){
        return platformGbStreamServiceFeign.del(param);
    }
}

