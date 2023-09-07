package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.CodeTokenConstant;
import cn.hutool.core.util.StrUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码登录TokenGranter
 *
 */
@Log4j2
public class CodeTokenGranter extends AbstractTokenGranter {


    private final AuthenticationManager authenticationManager;


    public CodeTokenGranter(AuthenticationManager authenticationManager,
                               AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, CodeTokenConstant.GRANT_TYPE);
    }
    protected CodeTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                  ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String VERIFY_CODE = parameters.get(CodeTokenConstant.KEY);
        String verificationCode = parameters.get(CodeTokenConstant.VERIFICATION_CODE);
        String username = parameters.get(CodeTokenConstant.USERNAME);
        String password = parameters.get(CodeTokenConstant.PASSWORD);
        if (StrUtil.isBlank(verificationCode)) {
            throw new UserDeniedAuthorizationException("请输入验证码！");
        }
        // 从Redis里读取存储的验证码信息
        if(!RedisUtils.hasKey(VERIFY_CODE)){
            throw new UserDeniedAuthorizationException("验证码不存在！");
        }
        String values = String.valueOf(RedisUtils.get(VERIFY_CODE));
        if (StringUtils.isEmpty(values)) {
            throw new UserDeniedAuthorizationException("验证码已过期！");
        }
        // 比较输入的验证码是否正确
        if (!StrUtil.equalsIgnoreCase(verificationCode, values)) {
            throw new UserDeniedAuthorizationException("验证码不正确！");
        }

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username,password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            log.error(ase);
            throw new UserDeniedAuthorizationException("账号或密码错误！");
        }
        // If the username/password are wrong the spec says we should send 400/invalid grant

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }




}
