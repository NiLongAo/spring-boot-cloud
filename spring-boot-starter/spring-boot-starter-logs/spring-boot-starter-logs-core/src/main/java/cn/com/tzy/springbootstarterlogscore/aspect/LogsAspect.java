package cn.com.tzy.springbootstarterlogscore.aspect;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterlogsbasic.annotation.ApiLog;
import cn.com.tzy.springbootstarterlogscore.core.LogApi;
import cn.com.tzy.springbootstarterlogscore.utils.IPUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Log4j2
@Aspect
public class LogsAspect {
    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("execution(public * cn.com.tzy.springboot*..*.*(..)) && @annotation(cn.com.tzy.springbootstarterlogsbasic.annotation.ApiLog)")
    public void apiLog(){}

    @Around(value="apiLog()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取请求方式
        String method = request.getMethod();
        //获取url
        String url = request.getRequestURI();
        //获取ip
        String ip = IPUtil.getIp(request);
        //获取请求参数
        NotNullMap param = new NotNullMap();
        //获取响应参数
        Object result;
        //获取持续时间
        long duration;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method signatureMethod = methodSignature.getMethod();
        ApiLog annotation = signatureMethod.getAnnotation(ApiLog.class);
        Integer type = annotation.type().getValue();
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
                param.put(i,point.getArgs()[i]);
            }
        }
        long start = System.currentTimeMillis();
        result = point.proceed();
        duration = System.currentTimeMillis() - start;
        //写入日志
        ThreadUtil.execute(()->{
            LogApi logApi = SpringUtil.getBean(LogApi.class);
            logApi.logs(type,method,url,ip,param,result,duration);
        });
        return result;
    }
}
