package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.access.MyDaoAuthenticationProvider;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms.SmsCodeAuthenticationSecurityConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini.WxMiniAuthenticationConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb.WxMiniWebConfig;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.converter.CustomJwtAccessTokenConverter;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter.CodeTokenGranter;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter.SmsCodeTokenGranter;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter.WxMiniGranter;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter.WxMiniWebGranter;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler.LoginFailureHandler;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler.LoginSuccessHandler;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.MyTokenServices;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Import({LoginFailureHandler.class, LoginSuccessHandler.class, SmsCodeAuthenticationSecurityConfig.class, WxMiniAuthenticationConfig.class, WxMiniWebConfig.class})
@AutoConfigureAfter(SecurityConfig.class)
@ConditionalOnProperty(value = "security-oauth.type",havingValue = "sso")
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final AuthorizationCodeServices authorizationCodeServices;
    private final PasswordEncoder passwordEncoder;
    private final BaseUserService userDetailsService;
    private final ClientDetailsService clientDetailsService;
    private final TokenStore tokenStore;

   /**
    * ObjectProvider
    * @param clientDetailsService
    */
   public AuthorizationConfig(AuthenticationManager authenticationManager,
                              AuthorizationCodeServices authorizationCodeServices,
                              PasswordEncoder passwordEncoder,
                              TokenStore tokenStore,
                              BaseUserService baseUserService,
                              ObjectProvider<ClientDetailsService> clientDetailsService
   ) {
       this.tokenStore =tokenStore;
       this.authenticationManager =authenticationManager;
       this.authorizationCodeServices = authorizationCodeServices;
       this.passwordEncoder = passwordEncoder;
       this.userDetailsService = baseUserService;
       this.clientDetailsService  = clientDetailsService.stream().collect(Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0)));
   }


    @Bean
    public MyDaoAuthenticationProvider authenticationProvider() {
        MyDaoAuthenticationProvider provider = new MyDaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false); // 用户不存在异常抛出
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * OAuth2客户端【数据库加载】
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 令牌访问端点
     * 配置令牌（token）的访问端点和令牌服务（token services）
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager) //配置密码模式需要
                .authorizationCodeServices(authorizationCodeServices) //配置授权码模式需要
                .userDetailsService(userDetailsService)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST) //允许post提交访问
                .tokenGranter(tokenGranter(endpoints, tokenServices()))
                .tokenServices(tokenServices()) //令牌管理服务
                .accessTokenConverter(jwtAccessTokenConverter())
                //刷新令牌再刷新后不更新刷新令牌 不更新令牌时间
                .reuseRefreshTokens(false);
    }

    /**
     * 令牌访问端点安全策略
     * 配置令牌端点的安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 表单认证,申请令牌
                .allowFormAuthenticationForClients()
                // spel表达式 访问公钥端点（/auth/token_key）需要认证
                .tokenKeyAccess("permitAll()")
                // spel表达式 访问令牌解析端点（/auth/check_token）需要认证
                .checkTokenAccess("permitAll()")
                //加解密
                .passwordEncoder(passwordEncoder);
    }

    /**
     * 令牌访问服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        MyTokenServices services = new MyTokenServices();
        services.setClientDetailsService(clientDetailsService);//客户端信息服务
        services.setSupportRefreshToken(true);//是否产生刷新令牌
        services.setReuseRefreshToken(false);
        services.setTokenStore(tokenStore);//令牌存储策略

        //令牌增强 使用JWT令牌方式
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        services.setTokenEnhancer(tokenEnhancerChain);
        services.setAccessTokenValiditySeconds(Constant.EXRP_HOUR*3);//设置默认值 令牌默认有效期3天
        services.setRefreshTokenValiditySeconds(Constant.EXRP_DAY*3);//设置默认值 刷新令牌默认有效期3天

        return services;
    }

    /**
     * 重点
     * 先获取已经有的五种授权，然后添加我们自己的进去
     *
     * @param endpoints AuthorizationServerEndpointsConfigurer
     * @return TokenGranter
     */
    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints, AuthorizationServerTokenServices tokenServices) {
        List<TokenGranter> granters = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
        // 验证码模式
        granters.add(new CodeTokenGranter(authenticationManager, tokenServices, endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        // 短信验证码模式
        granters.add(new SmsCodeTokenGranter(authenticationManager, tokenServices, endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        // 微信小程序模式
        granters.add(new WxMiniGranter(authenticationManager, tokenServices, endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        // 微信小程序openId模式
        granters.add(new WxMiniWebGranter(authenticationManager, tokenServices, endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        return new CompositeTokenGranter(granters);
    }


    /**
     * 使用非对称加密算法对token签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new DefaultUserAuthenticationConverter();
        defaultUserAuthenticationConverter.setUserDetailsService(userDetailsService);
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
        converter.setAccessTokenConverter(defaultAccessTokenConverter);
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "tongzeyong".toCharArray());
        KeyPair keyPair = factory.getKeyPair("jwt", "tongzeyong".toCharArray());
        return keyPair;
    }


}
