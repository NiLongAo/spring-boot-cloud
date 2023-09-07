package cn.com.tzy.springbootcomm.excption;

/**
 * @author Xiaohan.Yuan
 * @version 1.0.0
 * @ClassName TimingException.java
 * @Description 定时异常
 * @createTime 2021年12月16日
 */
public class TimingException extends RuntimeException {
    public TimingException(String message) {
        super(message);
    }
}
