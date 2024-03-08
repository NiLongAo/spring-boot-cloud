package cn.com.tzy.springbootactiviti.config.aspect;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.hutool.core.map.MapUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Aspect
@Component
@Log4j2
public class SecurityAspect {
    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("execution(public * cn.com.tzy.springbootactiviti..*.*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springbootactiviti..*.*(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springbootactiviti..*.*(..)) && @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springbootactiviti..*.*(..)) && @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMapping(){}
    @Pointcut("execution(public * cn.com.tzy.springbootactiviti..*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping(){}

    @Around(value="postMapping() || getMapping() || putMapping() || deleteMapping() || requestMapping()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Map<String, String> map = JwtUtils.getJwtUserMap();
        SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return map;
            }

            @Override
            public Object getPrincipal() {
                return MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
            @Override
            public String getName() {
                return MapUtil.getStr(map, JwtCommon.JWT_USER_NAME);
            }
        }));
        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(MapUtil.getStr(map, JwtCommon.JWT_USER_ID));
        return  point.proceed();
    }
}