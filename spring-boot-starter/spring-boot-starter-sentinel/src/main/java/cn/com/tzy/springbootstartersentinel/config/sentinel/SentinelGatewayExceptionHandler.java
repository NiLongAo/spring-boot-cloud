package cn.com.tzy.springbootstartersentinel.config.sentinel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** 熔断限流自定义异常 */
public class SentinelGatewayExceptionHandler extends AbstractSentinelExceptionHandler implements BlockRequestHandler {


  @Override
  public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable e) {
    RestResult<?> handle = handle(e);
    return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(handle));
  }
}
