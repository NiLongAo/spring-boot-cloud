package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import cn.com.tzy.springbootfeignvideo.api.video.StreamPushServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 推流相关信息接口
 */
@Service
public class StreamPushService {

    @Resource
    private StreamPushServiceFeign streamPushServiceFeign;
    /**
     * 分页
     */
    public PageResult page(StreamPushPageParam param){
        return streamPushServiceFeign.page(param);
    }

    /**
     * 详情
     */
    public RestResult<?> detail(Long id){
        return streamPushServiceFeign.detail(id);
    }

    /**
     * 保存
     */
    public RestResult<?> save(StreamPushSaveParam param){
        return streamPushServiceFeign.save(param);
    }


    /**
     * 移除
     */
    public RestResult<?> remove(Long id){
        return streamPushServiceFeign.remove(id);
    }

    /**
     * 获取推流播放地址
     */
    public RestResult<?> getPlayUrl(Long id){
        return streamPushServiceFeign.getPlayUrl(id);
    }

}
