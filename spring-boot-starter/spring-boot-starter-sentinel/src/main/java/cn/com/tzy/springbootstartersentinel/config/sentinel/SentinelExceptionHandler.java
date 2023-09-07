package cn.com.tzy.springbootstartersentinel.config.sentinel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 熔断限流自定义异常 */
public class SentinelExceptionHandler extends AbstractSentinelExceptionHandler implements BlockExceptionHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
    RestResult<?> handle = handle(e);
    // http状态码
    httpServletResponse.setStatus(HttpStatus.OK.value());
    httpServletResponse.setCharacterEncoding("utf-8");
    httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
    httpServletResponse.setContentType("application/json;charset=utf-8");
    // spring mvc自带的json操作工具，叫jackson
    new ObjectMapper().writeValue(httpServletResponse.getWriter(), handle);
  }

}
