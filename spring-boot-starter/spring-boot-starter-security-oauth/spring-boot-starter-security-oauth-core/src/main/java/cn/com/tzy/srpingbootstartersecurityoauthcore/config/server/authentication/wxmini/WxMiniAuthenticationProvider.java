package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxMaUserInfoVo;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.BaseUserService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;


@Log4j2
@AllArgsConstructor
public class WxMiniAuthenticationProvider implements AuthenticationProvider{

    private final WxMaService wxMaService;
    private final BaseUserService baseUserService;


    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //认证代码，认证通过返回认证对象，失败返回null
        WxMiniAuthenticationToken token = (WxMiniAuthenticationToken) authentication;
        if(StringUtils.isEmpty(token.getCode())){
            throw new InternalAuthenticationServiceException("未获取微信code");
        }
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(token.getCode());
        log.info("微信获取授权信息：{}",sessionInfo.toString());
        //效验用户信息
        if(!wxMaService.getUserService().checkUserInfo(sessionInfo.getSessionKey(),token.getRawData(),token.getSignature())){
            log.error("效验用户信息失败 sessionKey:{},rawData:{},signature:{}",sessionInfo.getSessionKey(),token.getRawData(),token.getSignature());
            throw new InternalAuthenticationServiceException("效验用户信息失败");
        }
        //解析用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionInfo.getSessionKey(), token.getEncryptedData(), token.getIv());
        log.info("微信用户信息解析：{}",userInfo.toString());
        WxMaUserInfoVo wxMaUserInfoVo = new WxMaUserInfoVo();
        BeanUtil.copyProperties(userInfo,wxMaUserInfoVo);
        wxMaUserInfoVo.setOpenId(sessionInfo.getOpenid());
        wxMaUserInfoVo.setScene(token.getScene());
        //处理用户信息
        OAuthUserDetails userDetails = (OAuthUserDetails) baseUserService.loadUserByUsername(JSONUtil.toJsonStr(wxMaUserInfoVo));
        //写入用户信息并返回认证类
        WxMiniAuthenticationToken smsCodeAuthenticationToken = new WxMiniAuthenticationToken(userDetails, userDetails.getAuthorities());
        smsCodeAuthenticationToken.setDetails(token.getDetails());
        return new WxMiniAuthenticationToken(userDetails,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        //Manager传递token给provider，调用本方法判断该provider是否支持该token。不支持则尝试下一个filter
        //本类支持的token类：UserPasswordAuthenticationToken
        return (WxMiniAuthenticationToken.class.isAssignableFrom(aClass));
    }

}
