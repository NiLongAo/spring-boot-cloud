package cn.com.tzy.springbootstartercloud.web.interceptor;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class LogonInterceptor extends HandlerInterceptorAdapter {
    static final String RESPONSE_TEXT = "{\"code\": %d, \"message\": \"%s\", \"data\": null}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HandlerMethod handlerMethod = null;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            write(RespCode.CODE_319,response);
            return false;
        }

        //NotLogin notLogin = handlerMethod.getMethod().getAnnotation(NotLogin.class);
        if (log.isDebugEnabled()) {
            log.debug("URL = {}", request.getRequestURI());
            log.debug("header payload = {} ", request.getHeader(Constant.JWT_PAYLOAD_KEY));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        response.setContentType(ConstEnum.ContentType.JSON.getValue());
    }


    private void write(RespCode code, HttpServletResponse response) throws IOException {
        response.setContentType(ConstEnum.ContentType.JSON.getValue());
        response.getOutputStream()
                .write(String.format(RESPONSE_TEXT, code.getValue(), code.getName()).getBytes(Constant.CHARSET_UTF_8));
    }
}
