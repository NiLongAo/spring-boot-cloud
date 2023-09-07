package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 级联平台管理(上级平台)
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/parent/platform",configuration = FeignConfiguration.class)
public interface ParentPlatformServiceFeign {

    /**
     * 获取注册到本服务的所有sip服务
     */
    @RequestMapping(value = "/sip_list",method = RequestMethod.GET)
    RestResult<?> findSipList();

    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody ParentPlatformPageParam param);


    /**
     * 详情
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);

    /**
     * 新增
     */
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody ParentPlatform param);


    /**
     * 修改
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody ParentPlatform param);

    /**
     * 删除
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    RestResult<?> delete(@RequestParam("id") Long id);
}
