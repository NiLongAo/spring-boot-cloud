package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface StreamProxyService extends IService<StreamProxy>{


    PageResult findPage(StreamProxyPageParam param);

    RestResult<?> save(StreamProxySaveParam param);

    RestResult<?> remove(Long id);

    RestResult<?> findFfmpegCmd(String mediaServerId);

    RestResult<?> start(Long id);

    RestResult<?> stop(Long id);

    RestResult<?> detail(Long id);

    RestResult<?> getPlayUrl(Long id);
}
