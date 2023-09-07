package cn.com.tzy.springbootactiviti.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Activiti工具类
 *
 * @author zp
 */
@Log4j2
@Component
public class ActivitiUtil {


    /**
     * 通过表达式获取其中的变量名
     *
     * @param expression 表达式
     * @return 变量名
     */
    public static String getVariableNameByExpression(String expression) {
        return expression.replace("${", "")
                .replace("}", "");
    }



}
