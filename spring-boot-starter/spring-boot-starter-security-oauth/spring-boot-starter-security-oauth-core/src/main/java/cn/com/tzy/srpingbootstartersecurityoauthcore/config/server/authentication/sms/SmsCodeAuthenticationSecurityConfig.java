package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms;

import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * * 自定义验证密码或者验证码
 */
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final BaseUserService userDetailsService;

    public SmsCodeAuthenticationSecurityConfig(BaseUserService baseUserService){
        this.userDetailsService = baseUserService;
    }

    @Override
    public void configure(HttpSecurity http) {
        // 过滤器
        SmsCodeAuthenticationFilter smsCodeCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        // 获取验证码提供者
        SmsCodeAuthenticationProvider SmsCodeCodeDaoAuthenticationProvider = new SmsCodeAuthenticationProvider(userDetailsService);

        // 将短信验证码校验器注册到 HttpSecurity， 并将短信验证码过滤器添加在 UsernamePasswordAuthenticationFilter 之前
        http.authenticationProvider(SmsCodeCodeDaoAuthenticationProvider)
                .addFilterAfter(smsCodeCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
