package cn.com.tzy.springbootwebapi.controller.config;


import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.config.OauthClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户端信息接口",position = 1)
@RestController("WebApiConfigOauthClientController")
@RequestMapping(value = "/webapi/config/oauth_client")
public class OauthClientController extends ApiController {

    @Autowired
    private OauthClientService oauthClientService;

    @ApiOperation(value = "客户端分页查询", notes = "客户端分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody OauthClientParam param){
        return oauthClientService.page(param);
    }

    @ApiOperation(value = "保存客户端信息", notes = "保存客户端信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated OauthClientParam params){
        return   oauthClientService.save(params);
    }

    @ApiOperation(value = "删除客户端信息", notes = "删除客户端信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="clientId", value="客户端编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("clientId") String clientId){
        return   oauthClientService.remove(clientId);
    }


    @ApiOperation(value = "根据客户端编号获取详情", notes = "根据客户端编号获取详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="clientId", value="客户端编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("/detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("clientId") String clientId) {
        return oauthClientService.detail(clientId);
    }



}
