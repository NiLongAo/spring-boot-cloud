package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class OAuth2AccessTokenJackson2Deserializer extends StdDeserializer<OAuth2AccessToken> {

    Logger logger = LoggerFactory.getLogger(OAuth2AccessTokenJackson2Deserializer.class);

    private static final long serialVersionUID = 1L;

    public OAuth2AccessTokenJackson2Deserializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public OAuth2AccessToken deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        String tokenValue = jsonNode.get(OAuth2AccessToken.ACCESS_TOKEN).asText();
        String tokenType = jsonNode.get(OAuth2AccessToken.TOKEN_TYPE).asText();
        Integer expiresIn = jsonNode.get(OAuth2AccessToken.EXPIRES_IN).asInt();
        String refreshToken = jsonNode.get(OAuth2AccessToken.REFRESH_TOKEN) == null ? null
            : jsonNode.get(OAuth2AccessToken.REFRESH_TOKEN).asText();
        String scopeText = jsonNode.get(OAuth2AccessToken.SCOPE).asText();
        Set<String> scope = OAuth2Utils.parseParameterList(scopeText);

        // TODO What should occur if a required parameter (tokenValue or tokenType) is missing?

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(tokenValue);
        accessToken.setTokenType(tokenType);
        if (expiresIn != null) {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000)));
        }
        if (refreshToken != null) {
            accessToken.setRefreshToken(new OAuth2ExpiringRefreshToken(refreshToken));
        }
        accessToken.setScope(scope);
        accessToken.setAdditionalInformation(additionalInformation);

        return accessToken;
    }
}
