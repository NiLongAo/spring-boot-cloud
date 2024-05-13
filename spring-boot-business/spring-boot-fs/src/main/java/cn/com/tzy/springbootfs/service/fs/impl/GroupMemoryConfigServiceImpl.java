package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.GroupMemoryConfig;
import cn.com.tzy.springbootfs.mapper.fs.GroupMemoryConfigMapper;
import cn.com.tzy.springbootfs.service.fs.GroupMemoryConfigService;
@Service
public class GroupMemoryConfigServiceImpl extends ServiceImpl<GroupMemoryConfigMapper, GroupMemoryConfig> implements GroupMemoryConfigService{

}
