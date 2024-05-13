package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.AgentGroup;
import cn.com.tzy.springbootfs.mapper.fs.AgentGroupMapper;
import cn.com.tzy.springbootfs.service.fs.AgentGroupService;
@Service
public class AgentGroupServiceImpl extends ServiceImpl<AgentGroupMapper, AgentGroup> implements AgentGroupService{

}
