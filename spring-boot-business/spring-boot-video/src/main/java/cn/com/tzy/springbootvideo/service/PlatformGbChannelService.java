package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PlatformGbChannelService extends IService<PlatformGbChannel>{


    RestResult<?> findDeviceChannelList(boolean administrator) throws Exception;

    RestResult<?> findChannelBindKey(PlatformGbChannelParam param,boolean administrator);

    RestResult<?> insert(PlatformGbChannelSaveParam param,boolean administrator);

    RestResult<?> delete(PlatformGbChannelSaveParam param,boolean administrator);


    void delPlatformGbChannel(String app,String stream);

    List<ParentPlatform> findChannelIdList(String channelId);
}
