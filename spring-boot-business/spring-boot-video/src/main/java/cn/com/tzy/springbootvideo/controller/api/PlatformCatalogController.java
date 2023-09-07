package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.PlatformCatalogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 上级平台目录相关接口
 */
@Log4j2
@RestController("ApiPlatformCatalogController")
@RequestMapping(value = "/api/platform/catalog")
public class PlatformCatalogController extends ApiController {

    @Resource
    private PlatformCatalogService platformCatalogService;

    /**
     * 获取目录树
     */
    @GetMapping("/tree")
    public RestResult<?> tree(@RequestParam("platformId") String platformId) throws Exception {
        return platformCatalogService.tree(platformId);
    }

    /**
     * 新增
     * @param param
     * @return
     */
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformCatalog param){
        return platformCatalogService.insert(param);
    }

    /**
     * 修改
     * @param param
     * @return
     */
    @PostMapping("update")
    public RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody PlatformCatalog param){
        return platformCatalogService.update(param);
    }

    /**
     * 删除
     * @return
     */
    @DeleteMapping("delete")
    public RestResult<?> delete(@RequestParam("id") String id){
        return platformCatalogService.delete(id);
    }
    /**
     * 删除关联
     * @param id 目录编号
     * @param type 删除类型 0.全部 1.通道 2.流
     * @return
     */
    @DeleteMapping("delete_relation")
    public RestResult<?> deleteRelation(@RequestParam("id") String id,@RequestParam("type") Integer type){
        return platformCatalogService.deleteRelation(id,type);
    }

}
