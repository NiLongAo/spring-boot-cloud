package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/oauth_client",configuration = FeignConfiguration.class)
public interface OauthClientServiceFeign {

    @RequestMapping(value = "page", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    PageResult page( @RequestBody OauthClientParam param);

    @RequestMapping(value = "/detail", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam(value = "clientId") String clientId);

    @RequestMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> save(@RequestBody OauthClientParam params);

    @RequestMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam(value = "clientId") String clientId);
}
