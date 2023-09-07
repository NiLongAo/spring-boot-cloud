package cn.com.tzy.springbootstartercloud.api;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.excption.RespException;
import cn.com.tzy.springbootcomm.spring.DateEditor;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * controller 统一处理类
 */
@RestControllerAdvice
@Log4j2
public class ApiController {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
    }


    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception, HttpServletResponse response) throws IOException {
        //参数错误拦截
        if (exception instanceof MethodArgumentNotValidException) {
            handleMethodArgumentNotValidException((MethodArgumentNotValidException) exception,response);//
        }
        //请求方式错误
        else if(exception instanceof HttpRequestMethodNotSupportedException){
            handleHttpRequestMethodNotSupportedException((HttpRequestMethodNotSupportedException) exception,response);
        }
        //数据库中已存在该记录错误
        else if(exception instanceof DuplicateKeyException){
            System.out.println(exception.getMessage());
            handleDuplicateKeyException((DuplicateKeyException) exception,response);
        }
        //数据库中已存在该记录错误
        else if(exception instanceof MaxUploadSizeExceededException){
            handleThisCustomizeException("文件大小超出10MB限制, 请压缩或降低文件质量!",response);
        }
        //数据库中已存在该记录错误
        else if(exception instanceof DataIntegrityViolationException){
            handleThisCustomizeException("字段太长,超出数据库字段的长度",response);
        }
        //自定义异常
        else if (exception instanceof RespException) {
            handleRespException((RespException) exception, response);
        }
        //数据库中已存在该记录错误
        else if(exception instanceof PoolException){
            handleThisCustomizeException("Redis 连接异常!",response);
        }
        //数据库中已存在该记录错误
        else if(exception instanceof NoHandlerFoundException){
            handleThisCustomizeException("路径不存在，请检查路径是否正确",response);
        }
        //通用拦截
        else {
            handleThisException(exception,response);
        }
        log.error("错误日志:",exception);
    }

    //参数校验拦截
    private void handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response) throws IOException {
        BindingResult bindingResult = exception.getBindingResult();
        List<String> errors = getAllErrors(bindingResult);
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(RestResult.result(RespCode.CODE_2.getValue(), StringUtils.join(errors, ", "))));
        response.flushBuffer();
    }

    //自定义错误消息错误拦截
    private void handleThisCustomizeException(String exceptionMessage, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(RestResult.result(RespCode.CODE_2.getValue(), exceptionMessage)));
        response.flushBuffer();
    }

    //通用错误拦截
    private void handleThisException(Exception exception, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(RestResult.result(RespCode.CODE_2.getValue(), exception.getMessage())));
        response.flushBuffer();
    }

    //自定义错误消息错误拦截
    private void handleRespException(RespException respException, HttpServletResponse response) throws IOException {
        RestResult<?> restResult = respException.code != null ? RestResult.result(respException.code, respException.message) : RestResult.result(RespCode.CODE_2.getValue(), respException.message);
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(restResult));
        response.flushBuffer();
    }

    private void handleDuplicateKeyException(DuplicateKeyException e,HttpServletResponse response) throws IOException {
        StringBuffer buffer = new StringBuffer();
        // 匹配数据库报错信息
        String regex = "Duplicate entry '(.*?)' for key '(idx|un|fk)_(.*?)'";
        String tableRegex = "(INSERT INTO|UPDATE) (.*?)  (\\(|SET) ";
        Matcher matcher = Pattern.compile(regex).matcher(e.getMessage());
        Matcher tableMatcher = Pattern.compile(tableRegex).matcher(e.getMessage());
        // 如果存在信息
        if (matcher.find() && tableMatcher.find()) {
            // 获取被占用的值
            String value = matcher.group(1);
            // 获取数据库表
            String table = tableMatcher.group(2);
            // 获取发生冲突的约束键
            String keys = matcher.group(3);
            // 数据库名下划线转驼峰
            // 扫描实体类
            Set<Class<?>> scan = ClassScanner.scanPackage("cn.com.tzy.springbootentity.dome");
            // 寻找冲突的字段的注解描述
            String description = scan.stream()
                    //.filter(BaseEntity.class::isAssignableFrom)
                    //.filter(c -> c.getName().toLowerCase().contains(table))
                    .filter(c -> {
                        TableName annotation = c.getAnnotation(TableName.class);
                        return annotation != null && table.equals(annotation.value());
                    })
                    .map(c -> {
                        // 获取实体类的属性值
                        Field[] declaredFields = c.getDeclaredFields();
                        for (Field declaredField : declaredFields) {
                            TableField annotation = declaredField.getAnnotation(TableField.class);
                             if(annotation == null || StringUtils.isEmpty(annotation.value())){
                                continue;
                            }
                            if(!keys.contains(annotation.value())){
                                continue;
                            }
                            ApiModelProperty annotation1 = declaredField.getAnnotation(ApiModelProperty.class);

                            return annotation1.value();
                        }
                        return "";
                    }).findFirst().orElse("");
            buffer.append(description).append(" ").append(value).append(" ");
        }
        buffer.append("字段已经被占用");
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(RestResult.result(RespCode.CODE_2.getValue(), buffer.toString())));
        response.flushBuffer();
    }

    public void handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletResponse response)throws IOException{
        StringBuffer sb = new StringBuffer();
        sb.append("不支持");
        sb.append(exception.getMethod());
        sb.append("请求方法，");
        sb.append("支持以下");
        String [] methods = exception.getSupportedMethods();
        if(methods!=null){
            for(String str:methods){
                sb.append(str);
                sb.append("、");
            }
        }
        response.setStatus(200);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().println(JSONUtil.toJsonStr(RestResult.result(RespCode.CODE_2.getValue(), sb.toString())));
        response.flushBuffer();
    }
    protected List<String> getAllErrors(BindingResult result) {
        List<String> list = Collections.emptyList();
        if(result.hasErrors()) {
            list = new ArrayList<String>();
            List<ObjectError> errorList = result.getAllErrors();
            for(ObjectError error : errorList){
                list.add(error.getDefaultMessage());
            }
        }
        return list;
    }
}
