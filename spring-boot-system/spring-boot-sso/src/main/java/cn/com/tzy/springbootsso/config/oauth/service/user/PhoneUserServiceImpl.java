package cn.com.tzy.springbootsso.config.oauth.service.user;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.BaseUser;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
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
import org.springframework.stereotype.Service;

import java.io.IOException;


@Order(2)
@Log4j2
@Service
public class PhoneUserServiceImpl implements UserDetailsService, UserDetailsTypeService {

    @Autowired
    private UserServiceFeign userServiceFeign;

    @Override
    public TypeEnum getTypeEnum() {
        return TypeEnum.WEB_MOBILE;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        RestResult<?> result = userServiceFeign.phone(phone);
        SecurityBaseUser sysUser = null;
        if (RespCode.CODE_0.getValue()==result.getCode()) {
            try {
                sysUser =  AppUtils.decodeJson2(AppUtils.encodeJson(result.getData()), SecurityBaseUser.class);
            } catch (IOException e) {
                throw new UsernameNotFoundException("用户:" + phone + ",json解析错误");
            }
        }
        //判断是否请求成功
        if (sysUser==null) {
            log.error("用户不存在服务");
            throw new UsernameNotFoundException("用户:" + phone + ",不存在!");
        }
        OAuthUserDetails oauthUserDetails = new OAuthUserDetails(sysUser);
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
