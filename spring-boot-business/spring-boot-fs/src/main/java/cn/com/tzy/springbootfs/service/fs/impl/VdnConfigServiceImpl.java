package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.VdnConfig;
import cn.com.tzy.springbootfs.mapper.fs.VdnConfigMapper;
import cn.com.tzy.springbootfs.service.fs.VdnConfigService;
@Service
public class VdnConfigServiceImpl extends ServiceImpl<VdnConfigMapper, VdnConfig> implements VdnConfigService{

}
