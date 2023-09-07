package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Config;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ConfigService extends IService<Config>{

    Config find(String k);

    PageResult page(ConfigParam param);

    RestResult<?> findAll();

    int update(Config config);

}

