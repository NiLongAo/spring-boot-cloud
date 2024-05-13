package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.AgentSip;
import cn.com.tzy.springbootfs.mapper.fs.AgentSipMapper;
import cn.com.tzy.springbootfs.service.fs.AgentSipService;
@Service
public class AgentSipServiceImpl extends ServiceImpl<AgentSipMapper, AgentSip> implements AgentSipService{

}
