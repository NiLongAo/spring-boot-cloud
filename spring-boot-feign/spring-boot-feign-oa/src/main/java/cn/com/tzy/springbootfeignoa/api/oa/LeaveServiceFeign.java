package cn.com.tzy.springbootfeignoa.api.oa;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.oa.LeaveParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "oa-server",contextId = "oa-server",path = "/api/oa/leave",configuration = FeignConfiguration.class)
public interface LeaveServiceFeign {

    @RequestMapping(value = "find",method = RequestMethod.GET)
    public RestResult<?> find(@RequestParam("id")Long id);

    @RequestMapping(value = "insert",method = RequestMethod.POST)
    public RestResult<?> insert(@RequestBody LeaveParam param);

    @RequestMapping(value = "updateState",method = RequestMethod.POST)
    public RestResult<?> updateState(@RequestBody LeaveParam param);

    @RequestMapping(value = "update_process_instance_id",method = RequestMethod.POST)
    public RestResult<?> updateProcessInstanceId(@RequestBody LeaveParam param);

    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public RestResult<?> delete(@RequestBody LeaveParam param);

}
