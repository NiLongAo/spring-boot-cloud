package cn.com.tzy.springbootstartercloud.web.aspect;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Log4j2
public class LoggerAspect {
    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping(){}

    @Around(value="postMapping() || getMapping() || putMapping() || deleteMapping() || requestMapping()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        if(log.isDebugEnabled()) {
            String methodName = point.getSignature().getName();
            log.debug("class: {} method: {}", point.getTarget().getClass(), methodName);
            if(point.getArgs() != null && point.getArgs().length > 0) {
                int length = point.getArgs().length;
                for(int i = 0; i < length; i++) {
                    if(point.getArgs()[i] instanceof HttpServletRequest
                            || point.getArgs()[i] instanceof HttpServletResponse
                            || point.getArgs()[i] instanceof BindingResult
                            || point.getArgs()[i] instanceof MultipartFile
                            || point.getArgs()[i] instanceof MultipartFile []
                    ) {
                        continue;
                    }
                    try {
                        log.debug("param: {} {}", i, AppUtils.encodeJson(point.getArgs()[i]));
                    } catch (Exception e) {
                        log.error("encodeJson error", e);
                    }
                }
            }
            Object result = point.proceed();
            if(result == null) {
                log.debug("response: null");
            } else {
                try {
                    log.debug("response: {}", AppUtils.encodeJson(result));
                } catch (Exception e) {
                    log.error("encodeJson error", e);
                }
            }
            return result;

        } else {
            return point.proceed();
        }
    }
}