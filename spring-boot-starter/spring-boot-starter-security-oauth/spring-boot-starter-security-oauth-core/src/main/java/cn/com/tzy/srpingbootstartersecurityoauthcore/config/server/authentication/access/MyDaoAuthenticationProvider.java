package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.access;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**
 * 实现密码加盐策略
 */

public class MyDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = authentication;
        if(userDetails instanceof OAuthUserDetails){
            OAuthUserDetails oAuthUserDetails = (OAuthUserDetails) userDetails;
            if(!StringUtils.isEmpty(oAuthUserDetails.getCredentialssalt())){
                //密码加盐策略
                String credentials = authentication.getCredentials().toString() + ((OAuthUserDetails) userDetails).getCredentialssalt();
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), credentials);
            }
        }
        super.additionalAuthenticationChecks(userDetails, usernamePasswordAuthenticationToken);
    }


    @Override
    public boolean supports(Class<?> aClass) {
        //Manager传递token给provider，调用本方法判断该provider是否支持该token。不支持则尝试下一个filter
        //本类支持的token类：UserPasswordAuthenticationToken
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}
