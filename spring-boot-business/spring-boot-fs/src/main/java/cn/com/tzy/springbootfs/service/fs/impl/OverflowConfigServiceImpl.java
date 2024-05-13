package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.OverflowConfig;
import cn.com.tzy.springbootfs.mapper.fs.OverflowConfigMapper;
import cn.com.tzy.springbootfs.service.fs.OverflowConfigService;
@Service
public class OverflowConfigServiceImpl extends ServiceImpl<OverflowConfigMapper, OverflowConfig> implements OverflowConfigService{

}
