package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface GbStreamService extends IService<GbStream>{


    GbStream findPlatformId(String platformId, String gbId);

    PageResult findPage(GbStreamPageParam param);
}
