package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Logs;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface LogsService extends IService<Logs>{


    PageResult pages(LogsParam param);

    RestResult<?> detail(Long id);

    RestResult<?> insert(LogsParam params);
}
