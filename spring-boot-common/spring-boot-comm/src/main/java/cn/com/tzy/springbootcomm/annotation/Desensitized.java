package cn.com.tzy.springbootcomm.annotation;


import cn.com.tzy.springbootcomm.common.enumcom.SensitiveTypeEnum;

import java.lang.annotation.*;

/**
 * 自定义注解
 * 数据脱敏
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Desensitized {
    //    脱敏类型(规则)
    SensitiveTypeEnum type();
}