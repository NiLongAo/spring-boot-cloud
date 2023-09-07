package cn.com.tzy.springbootwebapi.service.notice;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import cn.com.tzy.springbootfeignsms.api.notice.PublicNoticeServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicNoticeService {

    @Autowired
    PublicNoticeServiceFeign publicNoticeServiceFeign;

    public PageResult page(PublicNoticeParam param){
        return publicNoticeServiceFeign.page(param);
    }

    public RestResult<?> insert(PublicNoticeParam param){return publicNoticeServiceFeign.insert(param);}

    public RestResult<?> update(PublicNoticeParam param){return publicNoticeServiceFeign.update(param);}

    public RestResult<?> remove(Long id){return publicNoticeServiceFeign.remove(id);}

    public RestResult<?> detail(Long id){return publicNoticeServiceFeign.detail(id);}

    public PageResult userPage(PublicNoticeParam param) {
        param.userId = JwtUtils.getUserId();
        return publicNoticeServiceFeign.userPage(param);
    }

    public RestResult<?> userReadNoticeDetail(Long id) {
        return publicNoticeServiceFeign.userReadNoticeDetail(JwtUtils.getUserId(),id);
    }
}
