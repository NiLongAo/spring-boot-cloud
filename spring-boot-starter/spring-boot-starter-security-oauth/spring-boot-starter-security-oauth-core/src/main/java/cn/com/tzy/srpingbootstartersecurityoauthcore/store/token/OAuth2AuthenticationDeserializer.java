package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OAuth2AuthenticationDeserializer extends JsonDeserializer<OAuth2Authentication> {
    Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationDeserializer.class);

    @Override
    public OAuth2Authentication deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        JsonNode oauth2RequestNode = jsonNode.get("oauth2Request");
        JsonNode userAuthenticationNode = jsonNode.get("userAuthentication");
        JsonNode detailsNode = jsonNode.get("details");

        // Missing type id when trying to resolve subtype of [simple type, class
        // org.springframework.security.oauth2.provider.TokenRequest]: missing type id property '@class' (for POJO
        // property 'refresh')
        ObjectNode object = (ObjectNode)oauth2RequestNode;
        object.remove("refresh");
        OAuth2Request oAuth2Request = mapper.readValue(object.traverse(mapper), OAuth2Request.class);

        Authentication authentication = parseAuthentication(mapper, userAuthenticationNode);
        OAuth2Authentication token = new OAuth2Authentication(oAuth2Request, authentication);

        Object details = mapper.readValue(detailsNode.traverse(mapper), Object.class);
        token.setDetails(details);
        return token;
    }

    private Authentication parseAuthentication(ObjectMapper mapper, JsonNode jsonNode)
        throws JsonParseException, JsonMappingException, IOException {
        JsonNode principalNode = jsonNode.get("principal");
        ObjectNode object = (ObjectNode)principalNode;
        object.remove("authorities");

        UserDetails principal = mapper.readValue(object.traverse(mapper), new TypeReference<UserDetails>() {});
        Object credentials =
            mapper.readValue(jsonNode.get("credentials").traverse(mapper), new TypeReference<Object>() {});
        Set<SimpleGrantedAuthority> grantedAuthorities =
            parseSimpleGrantedAuthorities(mapper, jsonNode.get("authorities"));

        Class<? extends Object> requestClass = principal.getClass();
        Field officeCodeListField = null;
        // 2.1 获取属性字段
        try {
            officeCodeListField = requestClass.getDeclaredField("authorities");
            // 2.3 设置属性值
            officeCodeListField.setAccessible(true);
            officeCodeListField.set(principal, grantedAuthorities);
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return new UsernamePasswordAuthenticationToken(principal, credentials, grantedAuthorities);
    }

    private Set<SimpleGrantedAuthority> parseSimpleGrantedAuthorities(ObjectMapper mapper, JsonNode jsonNode)
        throws JsonParseException, JsonMappingException, IOException {
        List<JsonNode> authorities =
            mapper.readValue(jsonNode.traverse(mapper), new TypeReference<List<JsonNode>>() {});
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>(0);
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach(s -> {
                if (s != null && !s.isEmpty()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(s.get("authority").asText()));
                }
            });
        }
        return grantedAuthorities;
    }

}
