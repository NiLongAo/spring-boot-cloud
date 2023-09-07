package cn.com.tzy.springbootgateway.error;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import com.alibaba.nacos.common.model.RestResult;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

public class MyErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    public MyErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 指定响应处理方法为Json处理方法
     * @param errorAttributes
     * @return
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 根据code获取对应的HttpStatus
     * @param errorAttributes
     * @return
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return (Integer)errorAttributes.get("code");
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        // 这里其实可以根据异常类型进行定制化逻辑
        Throwable error = super.getError(request);
        Map<String, Object> errorAttributes = new HashMap<>(8);
        errorAttributes.put("message", error.getMessage());
        errorAttributes.put("code", RespCode.CODE_2.getValue());
        errorAttributes.put("method", request.methodName());
        errorAttributes.put("path", request.path());
        return errorAttributes;
    }

    /**
     * 初始化错误信息
     * @param request
     * @param e
     * @return
     */
    private String buildMessage(ServerRequest request, Throwable e){
        StringBuilder message = new StringBuilder("Failed to handle request[");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if(e != null){
            message.append(": ");
            message.append(e.getMessage());
        }
        return message.toString();
    }

    public static RestResult<?> response(int status,String errorMessage){
        return new RestResult(status,errorMessage);
    }

}
