package cn.com.tzy.springbootcomm.common.vo;


import lombok.*;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResult<T>  implements Serializable {
    private int code;
    private String message;
    private T data;

    private String tid = TraceContext.traceId();

    public RestResult(int code, String message, T data) {
        this.code = code;
        this.setMessage(message);
        this.data = data;
    }

    public RestResult(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public RestResult(int code, String message) {
        this.code = code;
        this.setMessage(message);
    }

    public boolean ok() {
        return this.code == RespCode.CODE_0.getValue();
    }

    public static <T> RestResult<T> result(RespCode respCode) {
        return new RestResult<T>(respCode.getValue(), respCode.getName());
    }

    public static <T> RestResult<T> result(int code, String message) {
        return new RestResult<T>(code, message);
    }
    public static <T> RestResult<T> result(int code, String message, T data) {
        return new RestResult<T>(code, message,data);
    }
}
