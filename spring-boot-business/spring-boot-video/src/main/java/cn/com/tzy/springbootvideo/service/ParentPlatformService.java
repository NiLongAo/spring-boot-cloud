package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ParentPlatformService extends IService<ParentPlatform>{

    List<Map> findChannelSource(String serverGbId, String channelId);

    List<ParentPlatform> queryPlatFormListForGBWithGBId(String channelId,List<String> allPlatformId);

    List<ParentPlatform> queryPlatFormListForStreamWithGBId(String app, String stream, List<String> allPlatformId);


    RestResult<?> findSipList();

    PageResult findPage(ParentPlatformPageParam param);

    RestResult<?> insert(ParentPlatform param);

    RestResult<?> update(ParentPlatform param);

    RestResult<?> delete(Long id);


    RestResult<?> detail(Long id);
}
