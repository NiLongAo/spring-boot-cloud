package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PlatformCatalogService extends IService<PlatformCatalog>{


    PlatformCatalog findId(String id);
    List<DeviceChannel> queryCatalogInPlatform(String serverGbId);

    List<String> findCatalogIdByAllSubList(String platformId,String catalogId);

    RestResult<?> tree(String platformId) throws Exception;

    RestResult<?> insert(PlatformCatalog param);

    RestResult<?> update(PlatformCatalog param);

    RestResult<?> delete(String id);

    /**
     * 删除关联
     * @param id 目录编号
     * @param type 删除类型 0.全部 1.通道 2.流
     * @return
     */
    RestResult<?> deleteRelation(String id, Integer type);
}
