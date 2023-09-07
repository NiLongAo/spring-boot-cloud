package cn.com.tzy.springbootsso.controller.oauth;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@Log4j2
public class OAuthController extends ApiController {

    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private CheckTokenEndpoint checkTokenEndpoint;
    /**
     * OAuth2认证
     */
    @PostMapping("/token")
    @ResponseBody
    public Object postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException, ParseException {
        OAuth2AccessToken body =  tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return RestResult.result(RespCode.CODE_0.getValue(),null,body);
    }

    /**
     * 验证token是否有效
     */
    @GetMapping("/check_token")
    @ResponseBody
    public Object checkToken(@RequestParam("token") String value) throws HttpRequestMethodNotSupportedException, ParseException {
        Map<String, ?> stringMap = checkTokenEndpoint.checkToken(value);
        return RestResult.result(RespCode.CODE_0.getValue(),null,stringMap);
    }
    /**
     * 获取公钥
     */
//    @GetMapping("/public-key")
//    public Map<String, Object> getPublicKey() {
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAKey key = new RSAKey.Builder(publicKey).build();
//        return new JWKSet(key).toJSONObject();
//    }


}
