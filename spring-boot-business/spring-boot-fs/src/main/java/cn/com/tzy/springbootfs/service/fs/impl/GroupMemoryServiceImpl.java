package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.GroupMemory;
import cn.com.tzy.springbootfs.mapper.fs.GroupMemoryMapper;
import cn.com.tzy.springbootfs.service.fs.GroupMemoryService;
@Service
public class GroupMemoryServiceImpl extends ServiceImpl<GroupMemoryMapper, GroupMemory> implements GroupMemoryService{

}
