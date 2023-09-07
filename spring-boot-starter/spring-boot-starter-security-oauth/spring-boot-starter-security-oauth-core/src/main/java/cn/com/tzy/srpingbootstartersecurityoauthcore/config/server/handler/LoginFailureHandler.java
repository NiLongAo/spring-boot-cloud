package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler;


import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFailureHandler  extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        logger.info("进入认证失败处理类");
		response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        RestResult<?> restResult = RestResult.result(RespCode.CODE_2.getValue(),exception.getMessage());
        response.getWriter().print(AppUtils.encodeJson2(restResult));
        response.getWriter().flush();
    }

}
