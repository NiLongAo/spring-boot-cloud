package cn.com.tzy.springbootbean.config.init;

import cn.com.tzy.springbootbean.mapper.sql.ConfigMapper;
import cn.com.tzy.springbootentity.dome.sys.Config;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 容器启动时保存系统配置
 */

@Component
public class ConfigInit{

    @Autowired
    ConfigMapper configMapper;

    public void init(AppConfig appConfig){
        List<Config> configs = configMapper.selectList(new QueryWrapper<Config>());
        Map<String,String>  allConfig = new HashMap<>();
        configs.forEach(obj->{
            allConfig.put(obj.getK(),obj.getV());
        });
        appConfig.setAllConfig(allConfig);
    }
}
