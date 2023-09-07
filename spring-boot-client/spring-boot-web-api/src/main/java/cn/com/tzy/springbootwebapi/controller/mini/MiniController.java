package cn.com.tzy.springbootwebapi.controller.mini;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.mini.MiniService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "微信web认证回调",position = 1)
@RestController("WebApiMiniController")
@RequestMapping(value = "/webapi/mini")
public class MiniController extends ApiController {

    @Resource
    private MiniService miniService;

    @ApiOperation(value = "web用户解绑小程序用户", notes = "web用户绑定小程序用户")
    @PostMapping("/unbind_mini_web")
    public RestResult<?> unbindMiniWeb(){
        Long userId = null;
        try{
            userId = JwtUtils.getUserId();
        }catch (Exception e){
            return RestResult.result(RespCode.CODE_2.getValue(),null,"当前用户信息解析失败");
        }
        return miniService.unbindMiniWeb(MiniUserParam.builder().userId(userId).build());
    }
}
