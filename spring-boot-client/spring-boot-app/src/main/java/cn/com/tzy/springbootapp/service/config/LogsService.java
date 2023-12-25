package cn.com.tzy.springbootapp.service.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import cn.com.tzy.springbootfeignbean.api.sys.LogsServiceFeign;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

@Service
public class LogsService {
    @Resource
    private LogsServiceFeign logsServiceFeign;

    public PageResult page(@Validated @RequestBody LogsParam param){return logsServiceFeign.page(param);}

    public RestResult<?> detail(Long id){return logsServiceFeign.detail(id);}
}
