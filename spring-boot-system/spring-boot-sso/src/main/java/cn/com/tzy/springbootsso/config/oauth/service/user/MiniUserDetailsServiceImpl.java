package cn.com.tzy.springbootsso.config.oauth.service.user;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.UserDetailsTypeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 【重要】从数据库获取用户信息，用于和前端传过来的用户信息进行密码判读
 * @author haoxr
 * @date 2020-05-27
 */
@Log4j2
@Order(1)
@Component
public class MiniUserDetailsServiceImpl implements UserDetailsService, UserDetailsTypeService {

    @Autowired
    private UserServiceFeign userServiceFeign;

    @Override
    public LoginTypeEnum getTypeEnum() {
        return LoginTypeEnum.APP_ACCOUNT;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RestResult<?> result = userServiceFeign.findLoginTypeByUserInfo(getTypeEnum(),username);
        SecurityBaseUser sysUser = null;
        if (RespCode.CODE_0.getValue()==result.getCode()) {
            try {
                sysUser =  AppUtils.decodeJson2(AppUtils.encodeJson(result.getData()), SecurityBaseUser.class);
            } catch (IOException e) {
                throw new RuntimeException("用户:" + username + ",Json解析失败");
            }
        }
        //判断是否请求成功
        if (sysUser==null) {
            log.error("用户不存在");
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }
        OAuthUserDetails oauthUserDetails = new OAuthUserDetails(sysUser);
        oauthUserDetails.setUsername(username);
        oauthUserDetails.setLoginType(getTypeEnum().getType());
        if (oauthUserDetails.getId() == null) {
            throw new UsernameNotFoundException(RespCode.CODE_311.getName());
        } else if (oauthUserDetails.getTenantId() == null) {
            throw new DisabledException("账户租户错误!");
        } else if (oauthUserDetails.getTenantStatus() == null || oauthUserDetails.getTenantStatus() != ConstEnum.Flag.YES.getValue()) {
            throw new DisabledException("账户租户已被禁用!");
        } else if (!oauthUserDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!oauthUserDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!oauthUserDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return oauthUserDetails;
    }
}
