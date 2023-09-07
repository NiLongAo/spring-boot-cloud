package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = OAuth2AuthenticationDeserializer.class)
public abstract class OAuth2AuthenticationMixin {}
