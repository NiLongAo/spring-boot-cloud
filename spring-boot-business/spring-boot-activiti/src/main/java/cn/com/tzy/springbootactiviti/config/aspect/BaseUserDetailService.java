package cn.com.tzy.springbootactiviti.config.aspect;

import cn.com.tzy.springbootactiviti.model.impl.OAuthUserDetails;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.message.AuthException;
@Log4j2
@Component
public  class BaseUserDetailService implements UserDetailsService {

    @Autowired
    private UserServiceFeign userServiceFeign;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if(attributes==null){
            throw new AuthException("获取不到当先请求");
        }
        RestResult<?> result = userServiceFeign.findLoginUserId(Long.valueOf(username));
        SecurityBaseUser sysUser = null;
        if (RespCode.CODE_0.getValue()==result.getCode()) {
            sysUser =  AppUtils.decodeJson2(AppUtils.encodeJson(result.getData()), SecurityBaseUser.class);
        }
        //判断是否请求成功
        if (sysUser==null) {
            log.error("用户不存在");
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }
        return new OAuthUserDetails(sysUser);
    }
}
