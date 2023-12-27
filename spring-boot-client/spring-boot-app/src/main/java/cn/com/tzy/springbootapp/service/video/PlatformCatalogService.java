package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootfeignvideo.api.video.PlatformCatalogServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 上级平台目录相关接口
 */
@Service
public class PlatformCatalogService {

    @Resource
    private PlatformCatalogServiceFeign platformCatalogServiceFeign;


    /**
     * 目录树
     */
    public RestResult<?> tree(String platformId){
        return platformCatalogServiceFeign.tree(platformId);
    }

    /**
     * 新增
     * @param param
     * @return
     */
    public RestResult<?> insert(PlatformCatalog param){
        return platformCatalogServiceFeign.insert(param);
    }


    /**
     * 修改
     * @param param
     * @return
     */
    public RestResult<?> update(PlatformCatalog param){
        return platformCatalogServiceFeign.update(param);
    }

    /**
     * 删除
     * @return
     */
    public RestResult<?> delete(String id){
        return platformCatalogServiceFeign.delete(id);
    }

    /**
     * 删除关联
     * @return
     */
    public RestResult<?> deleteRelation( String id,Integer type){
        return platformCatalogServiceFeign.deleteRelation(id,type);
    }

}
