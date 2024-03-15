package cn.com.tzy.springbootfeignsso.api.oauth;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sso-server",contextId = "sso-server",path = "/",configuration = FeignConfiguration.class)
public interface OAuthUserServiceFeign {

    /**
     * 授权码登录
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 authorization_code
     * @param code 授权码
     * @param redirectUri 跳转地址
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> tokenAuthorizationCode (@RequestParam(value = "client_id")String client_id,
                      @RequestParam(value = "client_secret") String client_secret,
                      @RequestParam(value = "login_type",defaultValue = "web_account")String login_type,
                      @RequestParam(value = "grant_type",defaultValue = "authorization_code") String grant_type,
                      @RequestParam(value = "code")String code,
                      @RequestParam(value = "redirectUri")String redirectUri);

    /**
     * 自定义验证码登录
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 code
     * @param key 验证码redis key
     * @param verificationCode 验证码
     * @param username 用户账号
     * @param password 用户密码
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> tokenCode (@RequestParam(value = "client_id")String client_id,
                      @RequestParam(value = "client_secret") String client_secret,
                      @RequestParam(value = "login_type",defaultValue = "web_account")String login_type,
                      @RequestParam(value = "grant_type",defaultValue = "code") String grant_type,
                      @RequestParam(value = "key")String key,
                      @RequestParam(value = "verificationCode")String verificationCode,
                      @RequestParam(value = "username")String username,
                      @RequestParam(value = "password")String password);

    /**
     * 自定义短信验证码登录
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 sms
     * @param SmsCodeCode 验证码信息
     * @param phone 手机号
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> tokenSms (@RequestParam(value = "client_id")String client_id,
                      @RequestParam(value = "client_secret") String client_secret,
                      @RequestParam(value = "login_type",defaultValue = "web_mobile")String login_type,
                      @RequestParam(value = "grant_type",defaultValue = "sms") String grant_type,
                      @RequestParam(value = "SmsCodeCode")String SmsCodeCode,
                      @RequestParam(value = "phone")String phone);

    /**
     * 刷新token
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 sms
     * @param refresh_token 验证码信息
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> refreshToken (@RequestParam(value = "client_id")String client_id,
                         @RequestParam(value = "client_secret") String client_secret,
                         @RequestParam(value = "login_type",defaultValue = "web_account")String login_type,
                         @RequestParam(value = "grant_type",defaultValue = "refresh_token") String grant_type,
                         @RequestParam(value = "refresh_token")String refresh_token);


    /**
     * 微信小程序登錄
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 mini
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> mini (@RequestParam(value = "client_id")String client_id,
                     @RequestParam(value = "client_secret") String client_secret,
                     @RequestParam(value = "login_type",defaultValue = "app_wx_mini")String login_type,
                     @RequestParam(value = "grant_type",defaultValue = "wx_mini") String grant_type,
                     @RequestParam(value = "code") String code,
                     @RequestParam(value = "sessionKey") String sessionKey,
                     @RequestParam(value = "encryptedData")String encryptedData,
                     @RequestParam(value = "signature")String signature,
                     @RequestParam(value = "iv")String iv,
                     @RequestParam(value = "rawData")String rawData,
                     @RequestParam(value = "scene")String scene);

    /**
     * 微信小程序web端登录
     * @param client_id 客户端账号
     * @param client_secret 客户端密码
     * @param grant_type 登录模式 mini
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> miniWeb (@RequestParam(value = "client_id")String client_id,
                     @RequestParam(value = "client_secret") String client_secret,
                     @RequestParam(value = "login_type",defaultValue = "wx_mini_web")String login_type,
                     @RequestParam(value = "grant_type",defaultValue = "wx_mini_web") String grant_type,
                     @RequestParam(value = "open_id") String openId);
    /**
     * 注销
     * @return RestResult
     */
    @RequestMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,method = RequestMethod.POST)
    RestResult<?> logout(@RequestParam(value = "login_type",defaultValue = "web_account")String login_type);

    /**
     * 注销
     * @return RestResult
     */
    @RequestMapping(value = "/oauth/check_token", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> checkToken(@RequestParam(value = "token")String token);
}
