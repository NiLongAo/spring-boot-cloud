package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.OauthClientService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.OauthClient;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("ApiConfigOauthClientController")
@RequestMapping(value = "/api/config/oauth_client")
public class OauthClientController extends ApiController {

    @Autowired
    private OauthClientService oauthClientService;


    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody OauthClientParam param){
        return oauthClientService.page(param);
    }

    /**
     * 保存字典类型
     * @param params
     * @return
     */
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated OauthClientParam params){
        return   oauthClientService.save(params.clientId,params.resourceIds,params.clientSecret,params.scope,params.authorizedGrantTypes,params.webServerRedirectUri,params.authorities,params.accessTokenValidity,params.refreshTokenValidity,params.additionalInformation,params.autoapprove);
    }

    /**
     * 删除字典类型
     * @param clientId
     * @return
     */
    @GetMapping("remove")
    public RestResult<?> remove(@RequestParam("clientId") String clientId){
        boolean b = oauthClientService.remove(new QueryWrapper<OauthClient>().eq("clientId", clientId));
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }


    @GetMapping("/detail")
    public RestResult<?> detail(@RequestParam("clientId") String clientId) {
        return oauthClientService.detail(clientId);
    }



}
