package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.ConfigMapper;
import cn.com.tzy.springbootbean.service.api.ConfigService;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Config;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {


    @Override
    @Cacheable(value = RedisCommon.CONFIG_INFO,key = " #k")
    public Config find(String k) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getK, k));
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
    @Cacheable(value = RedisCommon.CONFIG_ALL_INFO)
    public RestResult<?> findAll() {
        List<NotNullMap> data = new ArrayList<>();
        List<Config> configList = baseMapper.selectList(new LambdaQueryWrapper<Config>().orderByDesc(Config::getConfigName, Config::getK));
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
    @CacheEvict(value = {RedisCommon.CONFIG_INFO,RedisCommon.CONFIG_ALL_INFO},allEntries = true)
    public int update(Config config) {
        return baseMapper.update(config,new LambdaQueryWrapper<Config>().eq(Config::getK, config.getK()));
    }
}

