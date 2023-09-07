package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.StreamPush;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StreamPushService extends IService<StreamPush>{


    PageResult findPage(StreamPushPageParam param);
    List<StreamPush> findMediaServiceNotGbId(String mediaServiceId);

    RestResult<?> save(StreamPushSaveParam param);

    RestResult<?> remove(Long id);

    RestResult<?> getPlayUrl(Long id);

    RestResult<?> detail(Long id);
}
