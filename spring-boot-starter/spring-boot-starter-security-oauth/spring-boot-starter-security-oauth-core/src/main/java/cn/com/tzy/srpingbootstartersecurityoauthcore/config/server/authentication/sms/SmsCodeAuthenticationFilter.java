package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.SmsCodeTokenConstant;
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
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    //初始化只有post请求可进
    public SmsCodeAuthenticationFilter() {
        super(new AntPathRequestMatcher(SmsCodeTokenConstant.SPRING_SECURITY_RESTFUL_LOGIN_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 获取请求中的参数值
        String mobile = obtainMobile(request);

        if (Objects.isNull(mobile)) {
            mobile = "";
        }

        mobile = mobile.trim();


        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        Authentication authenticate = this.getAuthenticationManager().authenticate(authRequest);
        return authenticate;
    }

    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 获取手机号
     */
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(SmsCodeTokenConstant.SPRING_SECURITY_RESTFUL_PHONE_KEY);
    }
}
