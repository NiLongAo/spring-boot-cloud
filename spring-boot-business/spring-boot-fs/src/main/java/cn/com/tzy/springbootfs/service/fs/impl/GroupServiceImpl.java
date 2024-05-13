package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.GroupMapper;
import cn.com.tzy.springbootentity.dome.fs.Group;
import cn.com.tzy.springbootfs.service.fs.GroupService;
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService{

}
