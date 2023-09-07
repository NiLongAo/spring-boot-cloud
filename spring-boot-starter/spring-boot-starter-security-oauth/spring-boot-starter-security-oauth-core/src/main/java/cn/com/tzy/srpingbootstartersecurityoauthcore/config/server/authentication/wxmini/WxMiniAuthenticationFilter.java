package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxLoginParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 手机号登录拦截
 */
@Log4j2
public class WxMiniAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    //初始化只有post请求可进
    public WxMiniAuthenticationFilter() {
        super(new AntPathRequestMatcher(WxMiniConstant.SPRING_SECURITY_RESTFUL_LOGIN_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 获取请求中的参数值
        WxLoginParam wxLoginParam = obtainMiniCode(request);
        if (Objects.isNull(wxLoginParam) ) {
            throw new AuthenticationServiceException("未获取微信登录参数");
        }
        log.info("WxLoginParam :{}",wxLoginParam.toString());
        WxMiniAuthenticationToken authRequest = new WxMiniAuthenticationToken(wxLoginParam);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, WxMiniAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 获取用户相关信息
     */
    protected WxLoginParam obtainMiniCode(HttpServletRequest request) {
        return WxLoginParam.builder()
                .code(request.getParameter(WxMiniConstant.MINI_CODE))
                .iv(request.getParameter(WxMiniConstant.MINI_VI))
                .rawData(request.getParameter(WxMiniConstant.MINI_RAW_DATA))
                .signature(request.getParameter(WxMiniConstant.MINI_SIGNATURE))
                .sessionKey(request.getParameter(WxMiniConstant.MINI_SESSION_KEY))
                .encryptedData(request.getParameter(WxMiniConstant.MINI_ENCRYPTED_DATA))
                .scene(request.getParameter(WxMiniConstant.MINI_SCENE))
                .build();
    }
}
