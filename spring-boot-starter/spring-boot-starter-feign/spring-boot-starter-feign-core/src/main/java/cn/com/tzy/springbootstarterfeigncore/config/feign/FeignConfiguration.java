package cn.com.tzy.springbootstarterfeigncore.config.feign;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class FeignConfiguration implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String authorization = JwtUtils.getAuthorization(true);
    if(StringUtils.isNotEmpty(authorization)){
      requestTemplate.header(JwtCommon.JWT_AUTHORIZATION_KEY, authorization);
    }
    Long schemasTenantId = JwtUtils.getSchemasTenantId();
    if(schemasTenantId != null){
      requestTemplate.header(Constant.SCHEMAS_TENANT_ID, String.valueOf(schemasTenantId));
    }
  }
}
