package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.ConfigMapper;
import cn.com.tzy.springbootbean.service.api.ConfigService;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Config;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Override
    public Config find(String k) {
        return baseMapper.selectOne(new QueryWrapper<Config>().eq("k", k));
    }

    @Override
    public PageResult page(ConfigParam param) {
        int total = baseMapper.findPageCount(param);
        List<Config> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putString("k", obj.getK());
            map.putString("configName", obj.getConfigName());
            map.putString("v", obj.getV());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }




    @Override
    public RestResult<?> findAll() {
        List<NotNullMap> data = new ArrayList<>();
        List<Config> configList = baseMapper.selectList(new QueryWrapper<Config>().orderByDesc("config_name", "k"));
        configList.forEach(obj-> {
            NotNullMap map = new NotNullMap();
            map.putString("k",obj.getK());
            map.putString("v",obj.getV());
            map.putString("configName",obj.getConfigName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Config config) {
        return baseMapper.update(config,new QueryWrapper<Config>().eq("k", config.getK()));
    }
}

