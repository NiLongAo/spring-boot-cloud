package cn.com.tzy.springbootstarterlogsbasic.annotation;

import cn.com.tzy.springbootstarterlogsbasic.enums.LogsTypeEnum;

import java.lang.annotation.*;

/**
 * 接口日志注解
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApiLog {
    /**
     * 日志类型
     * @return
     */
    LogsTypeEnum type();
}
