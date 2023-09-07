package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;


@Log4j2
@AllArgsConstructor
public class SmsCodeAuthenticationProvider implements AuthenticationProvider{

    private final BaseUserService baseUserService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //认证代码，认证通过返回认证对象，失败返回null
        SmsCodeAuthenticationToken token = (SmsCodeAuthenticationToken) authentication;
        if(StringUtils.isEmpty(token.getPhone())){
            throw new InternalAuthenticationServiceException("手机号或验证码错误");
        }
        OAuthUserDetails userDetails = (OAuthUserDetails) baseUserService.loadUserByUsername(token.getPhone());
        //写入用户信息并返回认证类
        SmsCodeAuthenticationToken smsCodeAuthenticationToken = new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());
        smsCodeAuthenticationToken.setDetails(token.getDetails());
        return new SmsCodeAuthenticationToken(userDetails,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        //Manager传递token给provider，调用本方法判断该provider是否支持该token。不支持则尝试下一个filter
        //本类支持的token类：UserPasswordAuthenticationToken
        return (SmsCodeAuthenticationToken.class.isAssignableFrom(aClass));
    }

}
