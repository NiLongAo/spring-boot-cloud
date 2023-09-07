package cn.com.tzy.springbootwebapi.service.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import cn.com.tzy.springbootfeignsms.api.sms.SmsConfigServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsConfigService {

    @Autowired
    SmsConfigServiceFeign smsConfigServiceFeign;

    public RestResult<?> all() {
        return smsConfigServiceFeign.all();
    }

    public PageResult page(SmsConfigParam param) {
       return smsConfigServiceFeign.page(param);
    }

    public RestResult<?> insert(SmsConfigParam param) {
        return smsConfigServiceFeign.insert(param);
    }

    public RestResult<?> update(SmsConfigParam param) {
        return smsConfigServiceFeign.update(param);
    }

    public RestResult<?> remove(Long id) {
        return smsConfigServiceFeign.remove(id);
    }

    public RestResult<?> detail(Long id) {
        return smsConfigServiceFeign.detail(id);
    }
}

