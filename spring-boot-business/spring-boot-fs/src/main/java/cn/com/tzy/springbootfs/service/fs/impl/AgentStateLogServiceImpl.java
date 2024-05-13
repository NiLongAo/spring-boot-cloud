package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.AgentStateLogMapper;
import cn.com.tzy.springbootentity.dome.fs.AgentStateLog;
import cn.com.tzy.springbootfs.service.fs.AgentStateLogService;
@Service
public class AgentStateLogServiceImpl extends ServiceImpl<AgentStateLogMapper, AgentStateLog> implements AgentStateLogService{

}
