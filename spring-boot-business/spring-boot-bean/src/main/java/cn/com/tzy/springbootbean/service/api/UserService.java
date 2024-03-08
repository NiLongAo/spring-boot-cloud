package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {
    /**
     * oauth2获取登录用户信息
     */
    RestResult<?> findLoginUserId(Long userId);
    /**
     * 根据小程序用户编号获取用户信息
     */
    RestResult<?> findLoginInfo();

    RestResult<?> findLoginTypeByUserInfo(LoginTypeEnum clientType, String userNo);
    /**
     * 获取用户基本信息
     *
     */
    RestResult<?> findUserInfo(Long userId);
    /**
     * 新增用户
     */
    RestResult<?> insert(User param,Integer isAdmin,Integer isEnabled);
    /**
     * 修改用户
     */
    RestResult<?> update(User param,Integer isAdmin,Integer isEnabled);
    /**
     * 删除用户
     */
    RestResult<?> remove(Long id);
    PageResult findPage(UserParam userPageModel);
    RestResult<?> findUserIdList(List<Long> idList);
    RestResult<?> findRoleIdList(List<Long> idList);
    RestResult<?> findDepartmentIdList(List<Long> idList);
    RestResult<?> findPositionIdList(List<Long> idList);
    PageResult choiceUserPage(UserParam userPageModel);
    RestResult<?> userSelect(List<Long> userIdList, String userName, Integer limit);
}





