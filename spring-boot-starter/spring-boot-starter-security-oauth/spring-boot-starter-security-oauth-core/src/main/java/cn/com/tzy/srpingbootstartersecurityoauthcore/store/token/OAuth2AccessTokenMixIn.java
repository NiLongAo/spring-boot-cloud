package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = OAuth2AccessTokenJackson2Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = OAuth2AccessTokenJackson2Deserializer.class)
public class OAuth2AccessTokenMixIn {}
