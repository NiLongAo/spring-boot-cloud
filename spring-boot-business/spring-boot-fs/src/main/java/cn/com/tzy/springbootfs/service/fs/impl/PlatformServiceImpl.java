package cn.com.tzy.springbootfs.service.fs.impl;

import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootfs.mapper.fs.PlatformMapper;
import cn.com.tzy.springbootfs.service.fs.PlatformService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, Platform> implements PlatformService{

}
