package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;

/**
 * 用于标识查询用户类型
 */
public interface UserDetailsTypeService {

    public TypeEnum getTypeEnum();

}
