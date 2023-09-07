package cn.com.tzy.springbootcomm.excption;

import cn.com.tzy.springbootcomm.common.vo.RespCode;

/**
 * 返回异常数据类
 * Created by chensy on 2017/6/10.
 */
public class RespException extends RuntimeException {

    public Integer code;
    public String message;

    public RespException() {
    }
    public RespException(RespCode respCode) {
        super(respCode.getName());
        this.code =respCode.getValue();
        this.message =respCode.getName();
    }

    public RespException(int code,String message) {
        super(message);
        this.code =code;
        this.message =message;
    }

    public RespException(String message) {
        super(message);
        this.message =message;
    }
}
