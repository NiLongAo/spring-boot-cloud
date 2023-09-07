package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.sys.Logs;
import cn.com.tzy.springbootbean.mapper.sql.LogsMapper;
import cn.com.tzy.springbootbean.service.api.LogsService;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogsServiceImpl extends ServiceImpl<LogsMapper, Logs> implements LogsService{

    @Override
    public PageResult pages(LogsParam param) {
        int total =  baseMapper.findPageCount(param);
        List<Logs> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        for (Logs obj : pageResult) {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putInteger("type", obj.getType());
            map.putString("ip", obj.getIp());
            map.putString("ipAttribution", obj.getIpAttribution());
            map.putString("method", obj.getMethod());
            map.putString("api", obj.getApi());
            map.putInteger("duration", obj.getDuration());
            map.putDateTime("createTime", obj.getCreateTime());
            data.add(map);
        }
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> detail(Long id) {
        Logs logs = baseMapper.selectById(id);
        return RestResult.result(RespCode.CODE_0.getValue(),null,logs);
    }

    @Override
    public RestResult<?> insert(LogsParam params) {
        Logs build = Logs.builder()
                .type(params.type)
                .ip(params.ip)
                .method(params.method)
                .api(params.api)
                .ipAttribution(params.ipAttribution)
                .param(params.param)
                .result(params.result)
                .duration(params.duration)
                .build();
        baseMapper.insert(build);
        return RestResult.result(RespCode.CODE_0.getValue(),"添加成功");
    }
}
