package cn.com.tzy.springbootwebapi.controller.bean;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.export.ExportEntity;
import cn.com.tzy.springbootentity.param.bean.*;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstarterlogsbasic.annotation.ApiLog;
import cn.com.tzy.springbootstarterlogsbasic.enums.LogsTypeEnum;
import cn.com.tzy.springbootwebapi.service.bean.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "用户信息相关接口",position = 1)
@RestController("WebApiBeanUserController")
@RequestMapping("/webapi/bean/user")
public class UserController extends ApiController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "获取当前登录用户相关信息", notes = "获取当前登录用户相关信息")
    @GetMapping("login_info")
    @ResponseBody
    public RestResult<?> getLoginInfo(){
        return userService.findLoginInfo();
    }

    /**
     * 用户信息下拉展示(动态搜索数据源)
     * @return
     */
    @ApiOperation(value = "用户信息下拉展示(动态搜索数据源)", notes = "用户信息下拉展示(动态搜索数据源)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="idList", value="查询编号", required=false, paramType="query", dataType="list", defaultValue=""),
            @ApiImplicitParam(name="name", value="名称", required=false, paramType="query", dataType="string", defaultValue=""),
            @ApiImplicitParam(name="limit", value="查询数量", required=true, paramType="query", dataType="int", example="0")
    })
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> select(@RequestParam(value = "idList",required = false) List<Long> idList, @RequestParam(value = "name",required = false)String name, @RequestParam("limit") Integer limit){
        return userService.select(idList,name,limit);
    }

    @ApiOperation(value = "根据用户信息编号删除", notes = "根据用户信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="用户信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("info")
    @ResponseBody
    public RestResult<?> getinfo(@RequestParam("id") Long id){
       return userService.findInfo(id);
    }


    @ApiOperation(value = "用户条件分页查询", notes = "用户条件分页查询")
    @PostMapping("choice_user_page")
    @ResponseBody
    public PageResult choiceUserPage(@Validated @RequestBody UserParam userPageModel){
        return userService.choiceUserPage(userPageModel);
    }

    @ApiOperation(value = "用户分页查询", notes = "用户分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody UserParam userPageModel){
        return userService.page(userPageModel);
    }


    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    @PostMapping("/getcode")
    public RestResult<?> getCode() throws Exception {
        // 获取到session
        return userService.getCode();
    }

    @ApiOperation(value = "账号密码登录", notes = "账号密码登录")
    @PostMapping("login")
    @ResponseBody
    @ApiLog(type = LogsTypeEnum.LOGIN)
    public RestResult<?> login(@RequestBody LoginParam param){
        return userService.login(param);
    }


    @ApiOperation(value = "注销登录", notes = "注销登录")
    @GetMapping("logout")
    @ResponseBody
    public RestResult<?> logout(){
        return userService.logout();
    }


    @ApiOperation(value = "新增用户", notes = "新增用户")
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated(BaseModel.add.class) @RequestBody UserParam param){
        return userService.insert(param);
    }


    @ApiOperation(value = "修改用户", notes = "修改用户")
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@Validated(BaseModel.edit.class) @RequestBody UserParam param){
        return userService.update(param);
    }

    @ApiOperation(value = "修改登陆用户个人信息", notes = "修改登陆用户个人信息")
    @PostMapping("update_login_user_info")
    @ResponseBody
    public RestResult<?> updateLoginUserInfo(@Validated(UserParam.updateInfo.class) @RequestBody UserParam param){
        UserParam build = UserParam.builder()
                .id(param.id)
                .userName(param.userName)
                .nickName(param.nickName)
                .phone(param.phone)
                .provinceId(param.provinceId)
                .cityId(param.cityId)
                .areaId(param.areaId)
                .memo(param.memo)
                .gender(param.gender)
                .build();
        return userService.update(build);
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "根据用户信息编号删除", notes = "根据用户信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="用户信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("delete")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        return userService.delete(id);
    }


    @ApiOperation(value = "获取用户角色信息", notes = "获取用户角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("user_connect_role")
    @ResponseBody
    public RestResult<?> findUserConnectRole(@RequestParam("userId")Long userId) {
        return userService.findUserConnectRole(userId);
    }

    @ApiOperation(value = "根据当前登陆用户获取绑定其他端登陆账户", notes = "根据当前登陆用户获取绑定其他端登陆账户")
    @GetMapping("find_user_bind")
    @ResponseBody
    public RestResult<?> findUserBind() {
        Long userId = JwtUtils.getUserId();
        return userService.findUserBind(userId);
    }

    @ApiOperation(value = "保存用户角色信息", notes = "保存用户角色信息")
    @PostMapping("save_user_role")
    @ResponseBody
    public RestResult<?> saveUserConnectRole(@Validated @RequestBody UserConnectRoleParam param) {
        return userService.saveUserConnectRole(param);
    }

    @ApiOperation(value = "获取用户部门信息", notes = "获取用户部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("user_connect_department")
    @ResponseBody
    public RestResult<?> findUserConnectDepartment(@RequestParam("userId")Long userId) {
        return userService.findUserConnectDepartment(userId);
    }

    @ApiOperation(value = "保存用户部门信息", notes = "保存用户部门信息")
    @PostMapping("save_user_department")
    @ResponseBody
    public RestResult<?> saveUserConnectDepartment(@Validated @RequestBody UserConnectDepartmentParam param) {
        return userService.saveUserConnectDepartment(param);
    }

    @ApiOperation(value = "获取用户职位信息", notes = "获取用户职位信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("user_connect_position")
    @ResponseBody
    public RestResult<?> findUserConnectPosition(@RequestParam("userId")Long userId) {
        return userService.findUserConnectPosition(userId);
    }
    @ApiOperation(value = "保存用户职位信息", notes = "保存用户职位信息")
    @PostMapping("save_user_position")
    @ResponseBody
    public RestResult<?> saveUserConnectPosition(@Validated @RequestBody UserConnectPositionParam param) {
        return userService.saveUserConnectPosition(param);
    }

    /**
     * 导出相关接口示例
     */

    @ApiOperation(value = "获取可导出字段", notes = "获取可导出字段")
    @GetMapping("find_export_entity_info")
    @ResponseBody
    public RestResult<?> findExportEntityInfo() {
        return userService.findExportEntityInfo();
    }

    /**
     * 根据条件查询生成excel
     * 返回excel地址
     */
    @ApiOperation(value = "根据条件查询生成excel", notes = "根据条件查询生成excel")
    @PostMapping("find_export_url")
    @ResponseBody
    public void findExportUrl(@Validated @RequestBody ExportEntity<UserParam> exportEntity, HttpServletResponse response) throws Exception {
        userService.findExportUrl(exportEntity,response);
    }


}
