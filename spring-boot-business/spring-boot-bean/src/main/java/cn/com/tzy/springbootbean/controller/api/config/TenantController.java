package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.convert.sys.TenantConvert;
import cn.com.tzy.springbootbean.service.api.TenantService;
import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiConfigTenantController")
@RequestMapping(value = "/api/config/tenant")
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
        tenantService.insertTenant(param);
        return RestResult.result(RespCode.CODE_0.getValue(),"新增成功");
    }

    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Validated(BaseModel.edit.class) TenantParam param){
        Tenant convert = TenantConvert.INSTANCE.convert(param);
        tenantService.updateTenant(convert);
        return RestResult.result(RespCode.CODE_0.getValue(),"编辑成功");
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        return tenantService.removeTenant(id);
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        Tenant tenant = tenantService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,tenant);
    }
}
