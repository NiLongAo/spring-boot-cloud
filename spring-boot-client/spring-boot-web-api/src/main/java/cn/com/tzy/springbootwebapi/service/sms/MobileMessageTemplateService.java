package cn.com.tzy.springbootwebapi.service.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import cn.com.tzy.springbootfeignsms.api.sms.MobileMessageTemplateServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMessageTemplateService {

    @Autowired
    MobileMessageTemplateServiceFeign mobileMessageTemplateServiceFeign;
    
    public PageResult page(MobileMessageTemplateParam param) {
        return mobileMessageTemplateServiceFeign.page(param);
    }

    
    public RestResult<?> insert(MobileMessageTemplateParam param) {
        return mobileMessageTemplateServiceFeign.insert(param);
    }

    
    public RestResult<?> update(MobileMessageTemplateParam param) {
        return mobileMessageTemplateServiceFeign.update(param);
    }

    
    public RestResult<?> remove(Long id) {
        return mobileMessageTemplateServiceFeign.remove(id);
    }

    
    public RestResult<?> detail(Long id) {
        return mobileMessageTemplateServiceFeign.detail(id);
    }
}


