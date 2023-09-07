package cn.com.tzy.springbootgateway.excption;

/**
 * 自定义异常类型
 *
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:28
 **/
public class CustomException extends RuntimeException {

    //错误代码
    public CustomException(String message) { super(message);}

}
