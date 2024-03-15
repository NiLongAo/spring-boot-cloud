package cn.com.tzy.springbootfeignsso.api.oauth;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sso-server",contextId = "sso-server",path = "/sso/mini",configuration = FeignConfiguration.class)
public interface MiniServiceFeign {

    /**
     * 获取微信小程序二维码信息
     * @return RestResult
     */
    @RequestMapping(value = "/get_qr_code", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> getQRCode (@RequestParam(value = "uuid") String uuid);

}
