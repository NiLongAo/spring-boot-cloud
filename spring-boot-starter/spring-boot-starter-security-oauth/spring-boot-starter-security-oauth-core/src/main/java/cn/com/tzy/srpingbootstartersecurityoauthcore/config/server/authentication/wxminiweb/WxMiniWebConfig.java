package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb;

import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * * 自定义验证密码或者验证码
 */
public class WxMiniWebConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final BaseUserService userDetailsService;

    public WxMiniWebConfig(BaseUserService baseUserService){
        this.userDetailsService = baseUserService;
    }

    @Override
    public void configure(HttpSecurity http) {
        // 过滤器
        WxMiniWebFilter wxMiniAuthenticationFilter = new WxMiniWebFilter();
        wxMiniAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        // 获取微信小程序登陆提供者
        WxMiniWebProvider wxMiniAuthenticationProvider = new WxMiniWebProvider(userDetailsService);

        // 将微信小程序校验器注册到 HttpSecurity， 并将短信验证码过滤器添加在 UsernamePasswordAuthenticationFilter 之前
        http.authenticationProvider(wxMiniAuthenticationProvider)
                .addFilterAfter(wxMiniAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
