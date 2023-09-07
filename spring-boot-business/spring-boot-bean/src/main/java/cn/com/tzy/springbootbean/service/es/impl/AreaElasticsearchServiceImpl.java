package cn.com.tzy.springbootbean.service.es.impl;

import cn.com.tzy.springbootbean.mapper.es.AreaEsMapper;
import cn.com.tzy.springbootbean.service.es.AreaElasticsearchService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.es.sys.Area;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;


@Service
public class AreaElasticsearchServiceImpl  implements AreaElasticsearchService {

    @Resource
    private AreaEsMapper areaEsMapper;

    @Override
    public RestResult<?> findAll() {
         List<Area> data = areaEsMapper.selectList(new LambdaEsQueryWrapper<Area>());
         return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }
}
