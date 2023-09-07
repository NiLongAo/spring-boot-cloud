package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformGbStream;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PlatformGbStreamService extends IService<PlatformGbStream>{

    RestResult<?> findGbStreamList() throws Exception;
    RestResult<?> findStreamBindKey(PlatformGbStreamParam param);
    RestResult<?> insert(PlatformGbStreamSaveParam param);

    RestResult<?> delete(PlatformGbStreamSaveParam param);

    void delPlatformGbStream(String app, String stream);
}
