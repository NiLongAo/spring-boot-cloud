package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.converter;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.Common;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthcore.store.token.OAuth2ExpiringOAuth2RefreshToken;
import cn.com.tzy.srpingbootstartersecurityoauthcore.store.token.OAuth2ExpiringRefreshToken;
import cn.hutool.core.map.MapUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.Assert;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义实现 Jwt的token生成  取消 jwt中权限相关 防止 jwt太长
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

    private boolean includeGrantType;
    private final JsonParser objectMapper = JsonParserFactory.create();
    private Signer signer;

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> response = new HashMap<String, Object>();
        OAuth2Request clientToken = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            //此处修改 生成token时 取消权限相关
            Authentication userAuthentication = authentication.getUserAuthentication();
            response.put(UserAuthenticationConverter.USERNAME, userAuthentication.getName());
            //response.putAll(userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
        } else {
            if (clientToken.getAuthorities()!=null && !clientToken.getAuthorities().isEmpty()) {
                response.put(UserAuthenticationConverter.AUTHORITIES, AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));
            }
        }
        if (token.getScope()!=null) {
            response.put(SCOPE, token.getScope());
        }
        if (token.getAdditionalInformation().containsKey(JTI)) {
            response.put(JTI, token.getAdditionalInformation().get(JTI));
        }
        if (token.getExpiration() != null) {
            response.put(EXP, token.getExpiration().getTime() / 1000);
        }

        if (includeGrantType && authentication.getOAuth2Request().getGrantType()!=null) {
            response.put(GRANT_TYPE, authentication.getOAuth2Request().getGrantType());
        }
        response.putAll(token.getAdditionalInformation());

        response.put(CLIENT_ID, clientToken.getClientId());
        if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
            response.put(AUD, clientToken.getResourceIds());
        }
        return response;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //JWT内容增强
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        Map<String, Object> info = new HashMap<>();
        info.put(TOKEN_ID, MapUtil.getStr(additionalInformation,TOKEN_ID));
        info.put(JTI, MapUtil.getStr(additionalInformation,JTI));
        if(authentication.getUserAuthentication().getPrincipal() instanceof OAuthUserDetails){
            OAuthUserDetails oAuthUserDetails = (OAuthUserDetails) authentication.getUserAuthentication().getPrincipal();
            info.put(Common.JWT_USER_ID, oAuthUserDetails.getId());
            info.put(Common.JWT_ADMIN, oAuthUserDetails.getIsAdmin());
            info.put(Common.JWT_TENANT_ID, oAuthUserDetails.getTenantId());
        }
        result.setAdditionalInformation(info);
        return enhanceCustom(result, authentication);
    }

    private OAuth2AccessToken enhanceCustom(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
        String tokenId = result.getValue();
        if (!info.containsKey(TOKEN_ID)) {
            info.put(TOKEN_ID, tokenId);
        }
        else {
            tokenId = (String) info.get(TOKEN_ID);
        }
        result.setAdditionalInformation(info);
        result.setValue(this.encode(result, authentication));
        OAuth2RefreshToken refreshToken = result.getRefreshToken();
        if (refreshToken != null) {
            DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(accessToken);
            encodedRefreshToken.setValue(refreshToken.getValue());
            // Refresh tokens do not expire unless explicitly of the right type
            encodedRefreshToken.setExpiration(null);
            try {
                Map<String, Object> claims = objectMapper.parseMap(JwtHelper.decode(refreshToken.getValue()).getClaims());
                if (claims.containsKey(TOKEN_ID)) {
                    encodedRefreshToken.setValue(claims.get(TOKEN_ID).toString());
                }
            }
            catch (IllegalArgumentException e) {
            }
            Map<String, Object> refreshTokenInfo = new LinkedHashMap<String, Object>(
                    accessToken.getAdditionalInformation());
            refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
            refreshTokenInfo.put(ACCESS_TOKEN_ID, tokenId);
            encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
            OAuth2ExpiringRefreshToken token = new OAuth2ExpiringRefreshToken(encode(encodedRefreshToken, authentication));
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
                encodedRefreshToken.setExpiration(expiration);
                token = new OAuth2ExpiringOAuth2RefreshToken(encode(encodedRefreshToken, authentication), expiration);
            }
            result.setRefreshToken(token);
        }
        return result;
    }


    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        try {
            content = objectMapper.formatMap(this.convertAccessToken(accessToken, authentication));
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        String token = JwtHelper.encode(content, signer).getEncoded();
        return token;
    }


    @Override
    public void setKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
        signer = new RsaSigner((RSAPrivateKey) privateKey);
        super.setKeyPair(keyPair);
    }

    //默认方法回去再查权限接口
//    @Override
//    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
//
//    }

}
