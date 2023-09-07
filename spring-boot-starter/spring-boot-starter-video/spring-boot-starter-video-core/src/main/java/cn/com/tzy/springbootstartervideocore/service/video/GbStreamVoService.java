package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.GbStreamVo;

import java.util.List;

public abstract class GbStreamVoService {

   public abstract GbStreamVo findPlatformId(String platformId, String gbId);

    public abstract GbStreamVo findAppStream(String app,String steamId);

    public abstract int delAppStream(String app,String steamId);


    public abstract int update(GbStreamVo param);

    // 目录与国标之间的关系
    public abstract List<DeviceChannelVo> queryGbStreamListInPlatform(String serverGbId,String gbId);
}
