package cn.com.tzy.springbootcomm.excption;

public class UserException extends RuntimeException {
    public UserException(String message){
        super(message);
    }
}
