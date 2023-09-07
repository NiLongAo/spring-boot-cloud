package cn.com.tzy.springbootstarterfeigncore.config.feign;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class FeignConfiguration implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String payload = JwtUtils.getPayload();
    if (StringUtils.isNotBlank(payload)) {
      requestTemplate.header(Constant.JWT_PAYLOAD_KEY, payload);
    }
    String authorization = JwtUtils.getAuthorization();
    if (StringUtils.isNotBlank(authorization)) {
      requestTemplate.header(Constant.AUTHORIZATION_KEY, authorization);
    }
    Long schemasTenantId = JwtUtils.getSchemasTenantId();
    if(schemasTenantId != null){
      requestTemplate.header(Constant.SCHEMAS_TENANT_ID, String.valueOf(schemasTenantId));
    }
  }
}
