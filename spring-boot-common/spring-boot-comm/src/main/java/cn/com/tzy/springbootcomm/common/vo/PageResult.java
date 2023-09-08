package cn.com.tzy.springbootcomm.common.vo;


import lombok.*;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.io.Serializable;


/**
 * @author TZY
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    public static final PageResult SUCCESS = new PageResult();

    int code;
    String message;
    String tid = TraceContext.traceId();
    PageModel data;

    public PageResult(int code, String message,PageModel data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static PageResult result(RespCode respCode) {
        return new PageResult(respCode.getValue(), respCode.getName(),null);
    }

    public static PageResult result(int code, String message) {
        return new PageResult(code, message,null);
    }
    public static PageResult result(int code,int total, String message, Object data) {
        return new PageResult(code, message,new PageModel(total,data));
    }

    public static class PageModel{
        public int total;
        public Object data;

        public PageModel(int total, Object data) {
            this.total = total;
            this.data = data;
        }
    }
}
