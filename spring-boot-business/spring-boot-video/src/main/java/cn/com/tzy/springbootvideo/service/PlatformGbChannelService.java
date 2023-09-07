package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PlatformGbChannelService extends IService<PlatformGbChannel>{


    RestResult<?> findDeviceChannelList() throws Exception;

    RestResult<?> findChannelBindKey(PlatformGbChannelParam param);

    RestResult<?> insert(PlatformGbChannelSaveParam param);

    RestResult<?> delete(PlatformGbChannelSaveParam param);


    void delPlatformGbChannel(String app,String stream);

}
