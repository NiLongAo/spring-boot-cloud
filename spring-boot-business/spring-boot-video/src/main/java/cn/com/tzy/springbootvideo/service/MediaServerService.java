package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.MediaServer;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface MediaServerService extends IService<MediaServer>{


    PageResult findPage(MediaServerPageParam param);

    RestResult<?> save(MediaServerSaveParam param);

    RestResult<?> remove(String id);

    RestResult<?> findPlayUrl(String deviceId, String channelId);

    RestResult<?> detail(String id);
}
