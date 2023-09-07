package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniWebTokenConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 手机号登录拦截
 */
@Log4j2
public class WxMiniWebFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    //初始化只有post请求可进
    public WxMiniWebFilter() {
        super(new AntPathRequestMatcher(WxMiniWebTokenConstant.SPRING_SECURITY_RESTFUL_LOGIN_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 获取请求中的参数值
        String openId = obtainMiniCode(request);

        WxMiniWebToken authRequest = new WxMiniWebToken(openId);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, WxMiniWebToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 获取用户相关信息
     */
    protected String obtainMiniCode(HttpServletRequest request) {
        return request.getParameter(WxMiniWebTokenConstant.MINI_OPEN_ID);
    }
}
