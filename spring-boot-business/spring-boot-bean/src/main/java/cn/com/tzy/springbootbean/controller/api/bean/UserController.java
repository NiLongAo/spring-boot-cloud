package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.convert.bean.UserConvert;
import cn.com.tzy.springbootbean.service.api.UserConnectDepartmentService;
import cn.com.tzy.springbootbean.service.api.UserConnectPositionService;
import cn.com.tzy.springbootbean.service.api.UserConnectRoleService;
import cn.com.tzy.springbootbean.service.api.UserService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.param.bean.UserConnectDepartmentParam;
import cn.com.tzy.springbootentity.param.bean.UserConnectPositionParam;
import cn.com.tzy.springbootentity.param.bean.UserConnectRoleParam;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息
 */
@RestController("ApiBeanUserController")
@RequestMapping(value = "/api/bean/user")
public class UserController extends ApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserConnectRoleService userConnectRoleService;
    @Autowired
    private UserConnectDepartmentService userConnectDepartmentService;
    @Autowired
    private UserConnectPositionService userConnectPositionService;

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @PostMapping("choice_user_page")
    @ResponseBody
    public PageResult choiceUserPage(@Validated @RequestBody UserParam userPageModel){
        return userService.choiceUserPage(userPageModel);
    }

    /**
     * 用户信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> userSelect(@RequestParam(value = "userIdList",required = false)List<Long> userIdList,@RequestParam(value = "userName",required = false)String userName,@RequestParam("limit") Integer limit){
        return userService.userSelect(userIdList,userName,limit);
    }

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody UserParam userPageModel){
        return userService.findPage(userPageModel);
    }

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @GetMapping("login_account")
    @ResponseBody
    public RestResult<?> findLoginAccount(@RequestParam("loginAccount")String loginAccount){
        return userService.findLoginAccount(loginAccount);
    }

    /**
     * 根据用户编号获取用户信息
     * @return
     */
    @GetMapping("login_user_id")
    @ResponseBody
    public RestResult<?> findLoginUserId(@RequestParam("userId")Long userId){
        return userService.findLoginUserId(userId);
    }

    /**
     * 根据小程序用户编号获取用户信息
     */
    @GetMapping("mp_user_id")
    @ResponseBody
    public RestResult<?> findMpUserId(@RequestParam("miniId")Long miniId){
        return userService.findMpUserId(miniId);
    }

    /**
     * 根据手机号获取用户信息
     * @return
     */
    @GetMapping("phone")
    @ResponseBody
    public RestResult<?> phone(@RequestParam("phone")String phone){
        return userService.phone(phone);
    }


    /**
     * 根据微信token获取用户信息
     * @param openId 微信openId
     * @return
     */
    @GetMapping("openId")
    @ResponseBody
    public RestResult<?> token(@RequestParam("openId") String openId){
        return userService.openId(openId);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    @ResponseBody
    public RestResult<?> info(@RequestParam("id")Long id){
        return userService.findUserInfo(id);
    }

    /**
     * 新增用户
     */
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated @RequestBody UserParam param){
        User convert = UserConvert.INSTANCE.convert(param);
        return userService.insert(convert,param.getIsAdmin(),param.getIsEnabled());
    }


    /**
     * 修改用户
     */
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@Validated @RequestBody UserParam param){
        User convert = UserConvert.INSTANCE.convert(param);
        return userService.update(convert,param.getIsAdmin(),param.getIsEnabled());
    }


    /**
     * 删除用户
     */
    @GetMapping("delete")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        return userService.remove(id);
    }
    /**
     * 获取用户角色信息
     */
    @GetMapping("user_connect_role")
    @ResponseBody
    public RestResult<?> findUserConnectRole(@RequestParam("userId")Long userId) {
        return userConnectRoleService.find(userId);
    }
    /**
     * 保存用户角色信息
     */
    @PostMapping("save_user_role")
    @ResponseBody
    public RestResult<?> saveUserConnectRole(@Validated @RequestBody UserConnectRoleParam param) {
        return userConnectRoleService.save(param);
    }

    /**
     * 获取用户部门信息
     */
    @GetMapping("user_connect_department")
    @ResponseBody
    public RestResult<?> findUserConnectDepartment(@RequestParam("userId")Long userId) {
        return userConnectDepartmentService.find(userId);
    }
    /**
     * 保存用户部门信息
     */
    @PostMapping("save_user_department")
    @ResponseBody
    public RestResult<?> saveUserConnectDepartment(@Validated @RequestBody UserConnectDepartmentParam save) {
        return userConnectDepartmentService.save(save);
    }

    /**
     * 获取用户职位信息
     */
    @GetMapping("user_connect_position")
    @ResponseBody
    public RestResult<?> findUserConnectPosition(@RequestParam("userId")Long userId) {
        return userConnectPositionService.find(userId);
    }
    /**
     * 保存用户职位信息
     */
    @PostMapping("save_user_position")
    @ResponseBody
    public RestResult<?> saveUserConnectPosition(@Validated @RequestBody UserConnectPositionParam param) {
        return userConnectPositionService.save(param);
    }


    /**
     * 根据用户Id获取用户集合
     */
    @GetMapping("find_user_id_list")
    @ResponseBody
    public RestResult<?> findUserIdList(@RequestParam("idList") List<Long> idList) {
        return userService.findUserIdList(idList);
    }

    /**
     * 根据角色Id获取用户集合
     */
    @GetMapping("find_role_id_list")
    @ResponseBody
    public RestResult<?> findRoleIdList(@RequestParam("idList") List<Long> idList) {
        return userService.findRoleIdList(idList);
    }

    /**
     * 根据部门Id获取用户集合
     */
    @GetMapping("find_department_id_list")
    @ResponseBody
    public RestResult<?> findDepartmentIdList(@RequestParam("idList") List<Long> idList) {
        return userService.findDepartmentIdList(idList);
    }

    /**
     * 根据职位Id获取用户集合
     */
    @GetMapping("find_position_id_list")
    @ResponseBody
    public RestResult<?> findPositionIdList(@RequestParam("idList") List<Long> idList) {
        return userService.findPositionIdList(idList);
    }

}
