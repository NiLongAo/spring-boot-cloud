package cn.com.tzy.springbootwebapi.controller.config;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.config.TenantService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "客户端信息接口",position = 1)
@RestController("WebApiConfigTenantController")
@RequestMapping(value = "/webapi/config/tenant")
public class TenantController extends ApiController {

    @Autowired
    private TenantService tenantService;

    /**
     * 租户信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> tenantSelect(@RequestParam(value = "tenantIdList",required = false) List<Long> tenantIdList,
                                   @RequestParam(value = "tenantName",required = false)String tenantName,
                                   @RequestParam("limit") Integer limit
    ){
        return tenantService.tenantSelect(tenantIdList,tenantName,limit);
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody TenantParam tenantParam){
        return tenantService.page(tenantParam);
    }

    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@RequestBody @Validated(BaseModel.add.class) TenantUserVo param){
        return tenantService.insert(param);
    }

    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Validated(BaseModel.edit.class) TenantParam param){

        return tenantService.update(param);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        return tenantService.remove(id);
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  tenantService.detail(id);
    }
}
