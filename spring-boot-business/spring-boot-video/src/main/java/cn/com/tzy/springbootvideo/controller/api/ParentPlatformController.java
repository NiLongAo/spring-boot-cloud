package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.ParentPlatformService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 级联平台管理(上级平台)
 */
@Log4j2
@RestController("ApiParentPlatformController")
@RequestMapping(value = "/api/parent/platform")
public class ParentPlatformController extends ApiController {

    @Resource
    private ParentPlatformService parentPlatformService;

    /**
     * 获取注册到本服务的所有sip服务
     */
    @GetMapping("sip_list")
    public RestResult<?> findSipList(){
        return parentPlatformService.findSipList();
    }

    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody ParentPlatformPageParam param){
        return parentPlatformService.findPage(param);
    }

    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return parentPlatformService.detail(id);
    }

    /**
     * 新增
     * @param param
     * @return
     */
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody ParentPlatform param){
        return parentPlatformService.insert(param);
    }

    /**
     * 修改
     * @param param
     * @return
     */
    @PostMapping("update")
    public RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody ParentPlatform param){
        return parentPlatformService.update(param);
    }

    /**
     * 删除
     * @return
     */
    @DeleteMapping("delete")
    public RestResult<?> delete(@RequestParam("id") Long id){
        return parentPlatformService.delete(id);
    }

}
