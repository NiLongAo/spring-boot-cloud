package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import cn.com.tzy.springbootfeignvideo.api.video.StreamProxyServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 拉流相关信息接口
 */
@Service
public class StreamProxyService {

    @Resource
    private StreamProxyServiceFeign streamProxyServiceFeign;
    
    /**
     * 分页
     */
    public PageResult page(StreamProxyPageParam param){
        return streamProxyServiceFeign.page(param);
    }

    /**
     * 详情
     */
    public RestResult<?> detail(Long id){
        return streamProxyServiceFeign.detail(id);
    }

    /**
     * 保存
     */
    public RestResult<?> save(StreamProxySaveParam param){
        return streamProxyServiceFeign.save(param);
    }

    /**
     * 移除
     */
    public RestResult<?> remove(Long id){
        return streamProxyServiceFeign.remove(id);
    }

    /**
     * 获取流媒体中ffmpeg.cmd模板
     */
    public RestResult<?> findFfmpegCmd(String mediaServerId){
        return streamProxyServiceFeign.findFfmpegCmd(mediaServerId);
    }

    /**
     * 启用代理
     */
    public RestResult<?> start(Long id){
        return streamProxyServiceFeign.start(id);
    }

    /**
     * 停用代理
     */
    public RestResult<?> stop(Long id){
        return streamProxyServiceFeign.stop(id);
    }

    /**
     * 获取拉流播放地址
     */
    public RestResult<?> getPlayUrl(Long id){
        return streamProxyServiceFeign.getPlayUrl(id);
    }
}
