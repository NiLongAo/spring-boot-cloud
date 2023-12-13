package cn.com.tzy.springbootapp.service.config;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignbean.api.sys.AreaServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService {

    @Autowired
    AreaServiceFeign areaServiceFeign;

    public RestResult<?> all(){
        return areaServiceFeign.all();
    }
}
