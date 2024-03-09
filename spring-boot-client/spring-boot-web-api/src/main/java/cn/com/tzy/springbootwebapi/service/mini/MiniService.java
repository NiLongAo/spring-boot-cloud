package cn.com.tzy.springbootwebapi.service.mini;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootfeignsso.api.oauth.MiniServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiniService {

    @Autowired
    MiniServiceFeign miniServiceFeign;
    @Autowired
    cn.com.tzy.springbootfeignbean.api.bean.MiniServiceFeign beanMiniService;


    public RestResult<?> unbindMiniWeb(MiniUserParam param){
        return beanMiniService.unbindMiniWeb(param);
    }

}
