package cn.com.tzy.springbootapp.controller.bean;

import cn.com.tzy.springbootapp.service.bean.UserService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.bean.LoginParam;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户信息相关接口")
@RestController("AppBeanUserController")
@RequestMapping("/app/bean/user")
public class UserController extends ApiController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "获取当前登录用户相关信息", notes = "获取当前登录用户相关信息")
    @GetMapping("login_info")
    @ResponseBody
    public RestResult<?> getLoginInfo(){
        return userService.findLoginInfo();
    }

    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    @PostMapping("/getcode")
    public RestResult<?> getCode(){
        // 获取到session
        return userService.getCode();
    }

    @ApiOperation(value = "微信小程序登录", notes = "微信小程序登录")
    @PostMapping("login")
    @ResponseBody
    public RestResult<?> login(@RequestBody LoginParam param, HttpServletRequest request){
        return userService.login(param);
    }

    @ApiOperation(value = "注销登录", notes = "注销登录")
    @GetMapping("logout")
    @ResponseBody
    public RestResult<?> logout(@RequestParam("loginType")LoginTypeEnum loginType){
        return userService.logout(loginType);
    }

    @ApiOperation(value = "修改用户", notes = "修改用户")
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody UserParam param){
        UserParam build = UserParam.builder()
                .id(JwtUtils.getUserId())
                .nickName(param.getNickName())
                .phone(param.getPhone())
                .build();
        return userService.update(build);
    }


}
