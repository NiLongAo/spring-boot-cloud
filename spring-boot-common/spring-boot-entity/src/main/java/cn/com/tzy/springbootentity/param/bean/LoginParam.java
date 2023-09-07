package cn.com.tzy.springbootentity.param.bean;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 登录用户信息
 */
@ApiModel("登录用户信息")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginParam extends BaseModel{

    @ApiModelProperty(value = "登录类型 ",notes = "" +
            "authorization_code：授权模式," +
            "password：密码模式," +
            "refresh_token：刷新token," +
            "implicit:简化模式," +
            "client_credentials:客户端模式,s" +
            "ms：短信验证码模式," +
            "code：验证码模式," +
            "wx_mini：微信小程序模式," +
            "wx_mini_web：微信小程序web模式"
    )
    public GrantType grantType;
    @ApiModelProperty(value = "授权码模式")
    public AuthorizationCode authorizationCode;
    @ApiModelProperty(value = "密码模式")
    public PASSWORD password;
    @ApiModelProperty(value = "刷新token")
    public RefreshToken refreshToken;
    @ApiModelProperty(value = "短信验证码模式")
    public Sms sms;
    @ApiModelProperty(value = "普通验证码模式")
    public Code code;
    @ApiModelProperty(value = "微信小程序模式")
    public WxMini mini;
    @ApiModelProperty(value = "微信小程序openId模式 //只限内部登录")
    public WxMiniWeb wxMiniWeb;


    public enum GrantType {
        authorization_code,
        password,
        implicit,
        refresh_token,
        client_credentials,
        sms,
        code,
        wx_mini,
        wx_mini_web,
        ;
    }

    @ApiModel("授权码模式")
    public class AuthorizationCode{
        @ApiModelProperty("认证码")
        public String code;
        @ApiModelProperty("跳转url")
        public String redirectUri;
    }
    @ApiModel("密码模式")
    public class PASSWORD{
        @ApiModelProperty("账号")
        public String username;
        @ApiModelProperty("密码")
        public String password;
    }
    @ApiModel("刷新token")
    public class RefreshToken{
        @ApiModelProperty("刷新token")
        public String refreshToken;
    }
    @ApiModel("短信验证码模式")
    public class Sms{
        @ApiModelProperty("短信验证码")
        public String SmsCodeCode;
        @ApiModelProperty("手机号")
        public String phone;
    }
    @ApiModel("普通验证码模式")
    public class Code{
        @ApiModelProperty("验证key")
        public String key;
        @ApiModelProperty("验证码")
        public String verificationCode;
        @ApiModelProperty("账号")
        public String username;
        @ApiModelProperty("密码")
        public String password;
    }

    @ApiModel("微信小程序模式")
    public class WxMini{
        @ApiModelProperty("小程序code")
        public String code;
        @ApiModelProperty("小程序sessionKey")
        public String sessionKey;
        @ApiModelProperty("小程序encryptedData")
        public String encryptedData;
        @ApiModelProperty("小程序signature")
        public String signature;
        @ApiModelProperty("小程序iv")
        public String iv;
        @ApiModelProperty("小程序rawData")
        public String rawData;
        @ApiModelProperty("小程序scene")
        public String scene;
    }

    @ApiModel("微信小程序模式")
    public static class WxMiniWeb{
        @ApiModelProperty("小程序openId")
        public String openId;
    }
}
