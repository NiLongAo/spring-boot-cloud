package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.service.fs.AgentService;
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService{

}
