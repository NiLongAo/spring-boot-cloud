package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;

/**
 * 用于标识查询用户类型
 */
public interface UserDetailsTypeService {

    public LoginTypeEnum getTypeEnum();

}
