package cn.com.tzy.springbootcomm.excption;

public class ParamException  extends RuntimeException{

    public ParamException(String message){
        super(message);
    }

    public ParamException(String message, Throwable cause){
        super(message, cause);
    }

    public ParamException(Throwable cause){
        super(cause);
    }
}
