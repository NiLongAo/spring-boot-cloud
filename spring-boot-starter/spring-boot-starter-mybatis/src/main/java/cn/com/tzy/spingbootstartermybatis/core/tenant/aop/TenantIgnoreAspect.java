package cn.com.tzy.spingbootstartermybatis.core.tenant.aop;

import cn.com.tzy.spingbootstartermybatis.core.tenant.context.TenantContextHolder;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 忽略多租户的 Aspect，基于 {@link } 注解实现，用于一些全局的逻辑。
 * 例如说，一个定时任务，读取所有数据，进行处理。
 * 又例如说，读取所有数据，进行缓存。
 *
 * @author 芋道源码
 */
@Aspect
@Log4j2
public class TenantIgnoreAspect {

    @Pointcut("@annotation(cn.com.tzy.spingbootstartermybatis.core.tenant.aop.TenantIgnore)")
    public void tenantIgnore(){}

    @Around(value = "tenantIgnore()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setIgnore(true);
            // 执行逻辑
            return joinPoint.proceed();
        } finally {
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

}
