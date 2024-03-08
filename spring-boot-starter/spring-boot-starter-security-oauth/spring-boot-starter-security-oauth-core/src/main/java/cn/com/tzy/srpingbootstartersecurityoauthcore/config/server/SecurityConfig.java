package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.properties.SecurityOauthProperties;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms.SmsCodeAuthenticationSecurityConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini.WxMiniAuthenticationConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb.WxMiniWebConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler.MyLogoutHandler;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.RedisAuthorizationCodeServices;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.UserDetailsTypeService;
import cn.com.tzy.srpingbootstartersecurityoauthcore.properties.WxMaProperties;
import cn.com.tzy.srpingbootstartersecurityoauthcore.store.TokenStoreConfig;
import cn.hutool.core.convert.Convert;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@Setter
@EnableConfigurationProperties({SecurityOauthProperties.class,WxMaProperties.class})
@AutoConfigureAfter(TokenStoreConfig.class)
@ConditionalOnClass({HttpServletRequest.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Resource
    private WxMiniAuthenticationConfig wxMiniAuthenticationConfig;
    @Resource
    private WxMiniWebConfig wxMiniWebConfig;
    @Resource
    private TokenStore tokenStore;
    @Resource
    private SecurityOauthProperties securityOauthProperties;
//    @Value("${server.servlet.context-path}")
//    private String serverName;

    /**
     * 安全拦截机制（最重要）
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().anyRequest()
                .and()
                .formLogin()
                //手机验证码登录
                .and().apply(smsCodeAuthenticationSecurityConfig)
                //微信小程序登陆
                .and().apply(wxMiniAuthenticationConfig)
                //微信小程序openId登陆
                .and().apply(wxMiniWebConfig)
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                    //退出拦截器
                .and().logout().logoutSuccessHandler(myLogoutHandler())
                .and()
                .authorizeRequests()
                .antMatchers(Convert.toStrArray(securityOauthProperties.getIgnoreUrls())).permitAll() //所有/oauth/**的请求全部放行
                .anyRequest().authenticated()//其他请求全部验证

                .and().csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 关闭Session机制
                //网关中不需要此参数 单体服务
                //.accessDecisionManager(new AffirmativeBased(Arrays.asList(new WebExpressionVoter(),new AuthorizationAccessDecisionVoter(tokenStore))))
                //网关中不需要此参数 单体服务
                //.and().addFilter(new JWTAuthenticationFilter(tokenStore,authenticationManagerBean(),serverName))
            ;


    }

    //授权码模式
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(){
        //return new InMemoryAuthorizationCodeServices(); //本地内存缓存
        return new RedisAuthorizationCodeServices(); //redis 缓存
    }

    //密码模式
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 密码编码器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 自定义认证异常响应数据
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(ConstEnum.ContentType.JSON.getValue());
            response.getWriter().println(AppUtils.encodeJson(RestResult.result(RespCode.CODE_313)));
            response.flushBuffer();
        };
    }

    @Bean
    public BaseUserService baseUserService(ObjectProvider<UserDetailsTypeService> userDetailsTypeServices){
        Map<String, UserDetailsService> collect = userDetailsTypeServices.stream()
                .collect(Collectors.toMap(o -> o.getTypeEnum().getType(), o -> (UserDetailsService) o));
        return new BaseUserService(collect);
    }

    @Bean
    public MyLogoutHandler myLogoutHandler(){
        return new MyLogoutHandler(tokenStore);
    }

}
