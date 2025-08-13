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

    private int code;
    private String message;
    private String tid = TraceContext.traceId();
    private Page data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page implements Serializable{
        private Object items;
        private int total;
    }

    public PageResult(int code, String message, Object data, int total) {
        this.code = code;
        this.message = message;
        this.data = Page.builder().items(data).total(total).build();
    }

    public static PageResult result(RespCode respCode) {
        return result(respCode.getValue(), respCode.getName());
    }

    public static PageResult result(int code, String message) {
        return result(code, message,null,0);
    }
    public static PageResult result(int code, String message, Object data,int total) {
        return new PageResult(code, message,data,total);
    }
}
