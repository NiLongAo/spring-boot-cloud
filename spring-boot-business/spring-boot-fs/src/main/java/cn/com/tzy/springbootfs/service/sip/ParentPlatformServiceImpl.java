package cn.com.tzy.springbootfs.service.sip;

import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootfs.convert.fs.PlatformConvert;
import cn.com.tzy.springbootfs.mapper.fs.PlatformMapper;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.ParentPlatformService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ParentPlatformServiceImpl extends ParentPlatformService {

    @Resource
    private PlatformMapper platformMapper;

    @Override
    public ConfigModel random() {
        Platform platform = platformMapper.selectOne(new LambdaQueryWrapper<Platform>().last("limit 1"));
        return PlatformConvert.INSTANCE.convert(platform);
    }
}
