package cn.com.tzy.springbootwebapi.service.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import cn.com.tzy.springbootfeignbean.api.sys.ConfigServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    ConfigServiceFeign configServiceFeign;

    public RestResult<?> all(){
       return configServiceFeign.all();
    }

    public RestResult<?> update(ConfigParam param){return configServiceFeign.update(param);}


    public PageResult page(ConfigParam param){return configServiceFeign.page(param);}

    public RestResult<?> detail(String k){return configServiceFeign.detail(k);}
}
