package cn.com.tzy.springbootwebapi.service.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import cn.com.tzy.springbootfeignbean.api.sys.OauthClientServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class OauthClientService {

    @Autowired
    OauthClientServiceFeign oauthClientServiceFeign;

    public PageResult page(@Validated @RequestBody OauthClientParam param){return oauthClientServiceFeign.page(param);}

    public RestResult<?> detail(@RequestParam(value = "clientId") String clientId){return oauthClientServiceFeign.detail(clientId);}

    public RestResult<?> save(@RequestBody @Validated OauthClientParam params){return oauthClientServiceFeign.save(params);}

    public RestResult<?> remove(@RequestParam("clientId") String clientId){return oauthClientServiceFeign.remove(clientId);}
}
