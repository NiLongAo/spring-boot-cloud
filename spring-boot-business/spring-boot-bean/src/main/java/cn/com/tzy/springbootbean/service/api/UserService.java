package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * oauth2获取登录用户信息
     *
     * @param username
     * @return
     */
    RestResult<?> findLoginAccount(String username);

    /**
     * oauth2获取登录用户信息
     *
     * @param userId
     * @return
     */
    RestResult<?> findLoginUserId(Long userId);
    /**
     * 获取用户基本信息
     *
     * @param userId 用户编号
     * @return
     */
    RestResult<?> findUserInfo(Long userId);

    /**
     * 新增用户
     *
     * @param param
     * @return
     */
    RestResult<?> insert(User param,Integer isAdmin,Integer isEnabled);

    /**
     * 修改用户
     *
     * @param param
     * @return
     */
    RestResult<?> update(User param,Integer isAdmin,Integer isEnabled);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    RestResult<?> remove(Long id);

    PageResult findPage(UserParam userPageModel);

    RestResult<?> phone(String phone);

    RestResult<?> openId(String token);

    RestResult<?> findUserIdList(List<Long> idList);

    RestResult<?> findRoleIdList(List<Long> idList);

    RestResult<?> findDepartmentIdList(List<Long> idList);

    RestResult<?> findPositionIdList(List<Long> idList);

    PageResult choiceUserPage(UserParam userPageModel);

    RestResult<?> userSelect(List<Long> userIdList, String userName, Integer limit);

}





