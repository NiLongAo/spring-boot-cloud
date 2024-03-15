package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.UserConnectDepartmentParam;
import cn.com.tzy.springbootentity.param.bean.UserConnectPositionParam;
import cn.com.tzy.springbootentity.param.bean.UserConnectRoleParam;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/user",configuration = FeignConfiguration.class)
public interface UserServiceFeign {
    @RequestMapping(value = "/login_type_by_user_info",method = RequestMethod.GET)
    RestResult<?> findLoginTypeByUserInfo(@RequestParam("loginAccount") LoginTypeEnum clientType, @RequestParam("userNo")String userNo);
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    RestResult<?> getInfo(@RequestParam(value = "id") Long id);

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    RestResult<?> select(@RequestParam(value = "userIdList",required = false)List<Long> userIdList,@RequestParam(value = "userName",required = false)String userName,@RequestParam("limit") Integer limit);

    @RequestMapping(value = "/login_user_id",method = RequestMethod.GET)
    RestResult<?> findLoginUserId(@RequestParam(value = "userId") Long userId);

    @RequestMapping(value = "/login_info",method = RequestMethod.GET)
    RestResult<?> findLoginInfo();

    @RequestMapping(value = "/choice_user_page",method = RequestMethod.POST)
    PageResult choiceUserPage(@RequestBody UserParam userPageModel);

    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@RequestBody UserParam userPageModel);

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@RequestBody UserParam userPageModel);

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    RestResult<?> update(@RequestBody UserParam userPageModel);

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    RestResult<?> delete(@RequestParam("id")Long id);


    @RequestMapping(value = "/find_user_id_list",method = RequestMethod.GET)
    RestResult<?> findUserIdList(@RequestParam("idList") List<Long> idList);

    @RequestMapping(value = "/find_role_id_list",method = RequestMethod.GET)
    RestResult<?> findRoleIdList(@RequestParam("idList") List<Long> idList);

    @RequestMapping(value = "/find_department_id_list",method = RequestMethod.GET)
    RestResult<?> findDepartmentIdList(@RequestParam("idList") List<Long> idList);

    @RequestMapping(value = "/find_position_id_list",method = RequestMethod.GET)
    RestResult<?> findPositionIdList(@RequestParam("idList") List<Long> idList);
    /**
     * 获取用户角色信息
     */
    @RequestMapping(value = "/user_connect_role",method = RequestMethod.GET)
    RestResult<?> findUserConnectRole(@RequestParam("userId")Long userId);
    /**
     * 保存用户角色信息
     */
    @RequestMapping(value = "/save_user_role",method = RequestMethod.POST)
    RestResult<?> saveUserConnectRole(@Validated @RequestBody UserConnectRoleParam param);
    /**
     * 获取用户部门信息
     */
    @RequestMapping(value = "/user_connect_department",method = RequestMethod.GET)
    RestResult<?> findUserConnectDepartment(@RequestParam("userId")Long userId);
    /**
     * 保存用户部门信息
     */
    @RequestMapping(value = "/save_user_department",method = RequestMethod.POST)
    RestResult<?> saveUserConnectDepartment(@Validated @RequestBody UserConnectDepartmentParam param);

    /**
     * 获取用户职位信息
     */
    @RequestMapping(value = "/user_connect_position",method = RequestMethod.GET)
    RestResult<?> findUserConnectPosition(@RequestParam("userId")Long userId);
    /**
     * 保存用户职位信息
     */
    @RequestMapping(value = "/save_user_position",method = RequestMethod.POST)
    RestResult<?> saveUserConnectPosition(@Validated @RequestBody UserConnectPositionParam param);

}
