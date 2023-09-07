package cn.com.tzy.springbootstarterfeignconfig.aspect;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.excption.RespException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/** Title: FeignExceptionAspect Description: Feign统一异常处理 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class FeignExceptionAspect {

  /**
   * Pointcut注解声明切点 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
   *
   * @within 对类起作用，@annotation 对方法起作用
   */
  @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
  public void feignClientPointCut() {}

  /**
   * 配置前置通知,使用在方法aspect()上注册的切入点 同时接受JoinPoint切入点对象,可以没有该参数
   *
   * @param proceedingJoinPoint
   * @throws ClassNotFoundException
   */
  @Around("feignClientPointCut()")
  public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Object object = proceedingJoinPoint.proceed();
    if (object instanceof RestResult) {
      RestResult<?> restResult = (RestResult) object;
      if (restResult.getCode() != RespCode.CODE_0.getValue() && restResult.getCode() != RespCode.CODE_2.getValue()) {
        throw new RespException(restResult.getCode(),restResult.getMessage());
      }
    }
    return object;
  }
}
