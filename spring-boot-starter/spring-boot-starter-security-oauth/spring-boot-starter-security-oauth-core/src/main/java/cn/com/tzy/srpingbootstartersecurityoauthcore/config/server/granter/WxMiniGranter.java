package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxLoginParam;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini.WxMiniAuthenticationToken;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class WxMiniGranter extends AbstractTokenGranter {

    private final AuthenticationManager authenticationManager;

    public WxMiniGranter(AuthenticationManager authenticationManager,
                         AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                         OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, WxMiniConstant.WX_MINI_TYPE);
    }

    protected WxMiniGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                            ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        WxLoginParam wxLoginParam = obtainMiniCode(parameters);
        if (ObjectUtil.isNull(wxLoginParam)) {
            throw new UserDeniedAuthorizationException("未获取用户微信code！");
        }
        Authentication userAuth = new WxMiniAuthenticationToken(wxLoginParam);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        // If the username/password are wrong the spec says we should send 400/invalid grant
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + wxLoginParam.getCode());
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }

    /**
     * 获取用户相关信息
     */
    protected WxLoginParam obtainMiniCode(Map<String, String> parameters) {
        return WxLoginParam.builder()
                .code(parameters.get(WxMiniConstant.MINI_CODE))
                .iv(parameters.get(WxMiniConstant.MINI_VI))
                .rawData(parameters.get(WxMiniConstant.MINI_RAW_DATA))
                .signature(parameters.get(WxMiniConstant.MINI_SIGNATURE))
                .sessionKey(parameters.get(WxMiniConstant.MINI_SESSION_KEY))
                .encryptedData(parameters.get(WxMiniConstant.MINI_ENCRYPTED_DATA))
                .scene(parameters.get(WxMiniConstant.MINI_SCENE))
                .build();
    }

}