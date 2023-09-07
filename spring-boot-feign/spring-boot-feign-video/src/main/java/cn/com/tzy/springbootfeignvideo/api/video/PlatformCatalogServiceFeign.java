package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 上级平台目录相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/platform/catalog",configuration = FeignConfiguration.class)
public interface PlatformCatalogServiceFeign {

    /**
     * 目录树
     */
    @RequestMapping(value = "/tree",method = RequestMethod.GET)
    RestResult<?> tree(@RequestParam("platformId") String platformId);

    /**
     * 新增
     * @param param
     * @return
     */
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformCatalog param);


    /**
     * 修改
     * @param param
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody PlatformCatalog param);

    /**
     * 删除
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    RestResult<?> delete(@RequestParam("id") String id);

    /**
     * 删除关联
     * @return
     */
    @RequestMapping(value = "/delete_relation",method = RequestMethod.DELETE)
    RestResult<?> deleteRelation(@RequestParam("id") String id,@RequestParam("type") Integer type);

}
