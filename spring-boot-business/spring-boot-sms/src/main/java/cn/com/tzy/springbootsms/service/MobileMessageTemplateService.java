package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.MobileMessageTemplate;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

public interface MobileMessageTemplateService extends IService<MobileMessageTemplate> {

    PageResult findPage(MobileMessageTemplateParam param);

    RestResult<?> insert(MobileMessageTemplateParam param);

    RestResult<?> update(MobileMessageTemplateParam param);

    RestResult<?> remove(Long id);

    RestResult<?> detail(Long id);

    MobileMessageTemplate findLast(Integer configId,Integer type);

}


