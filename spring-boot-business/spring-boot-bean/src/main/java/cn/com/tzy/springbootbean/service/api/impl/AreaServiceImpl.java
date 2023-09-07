package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.config.init.AppConfig;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootbean.mapper.sql.AreaMapper;
import cn.com.tzy.springbootentity.dome.sys.Area;
import cn.com.tzy.springbootbean.service.api.AreaService;
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService{

    @Autowired
    AppConfig appConfig;
    @Override
    public RestResult<?> findAreaAll() {
        return RestResult.result(RespCode.CODE_0.getValue(),null,appConfig.getAllArea());
    }
}
