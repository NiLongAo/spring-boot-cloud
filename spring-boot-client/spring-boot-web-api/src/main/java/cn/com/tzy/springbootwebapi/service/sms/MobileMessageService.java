package cn.com.tzy.springbootwebapi.service.sms;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import cn.com.tzy.springbootfeignsms.api.sms.MobileMessageServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMessageService {

    @Autowired
    MobileMessageServiceFeign mobileMessageServiceFeign;
    
    public PageResult page(MobileMessageParam param) {
        return mobileMessageServiceFeign.page(param);
    }

    
    public RestResult<?> detail(Long id) {
        return mobileMessageServiceFeign.detail(id);
    }

}

