package cn.com.tzy.springbootcomm.excption;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {


    public BizException(String message){
        super(message);
    }

    public BizException(String message, Throwable cause){
        super(message, cause);
    }

    public BizException(Throwable cause){
        super(cause);
    }
}
