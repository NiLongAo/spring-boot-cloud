package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniWebTokenConstant;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb.WxMiniWebToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class WxMiniWebGranter extends AbstractTokenGranter {

    private final AuthenticationManager authenticationManager;

    public WxMiniWebGranter(AuthenticationManager authenticationManager,
                            AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                            OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, WxMiniWebTokenConstant.WX_MINI_WEB_TYPE);
    }

    protected WxMiniWebGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                               ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String openId = parameters.get(WxMiniWebTokenConstant.MINI_OPEN_ID);

        Authentication userAuth = new WxMiniWebToken(openId);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        // If the username/password are wrong the spec says we should send 400/invalid grant
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate openId: " + openId);
        }
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }


}