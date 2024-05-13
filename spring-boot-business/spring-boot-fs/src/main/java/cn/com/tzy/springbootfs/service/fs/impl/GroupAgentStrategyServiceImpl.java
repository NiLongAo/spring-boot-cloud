package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.GroupAgentStrategy;
import cn.com.tzy.springbootfs.mapper.fs.GroupAgentStrategyMapper;
import cn.com.tzy.springbootfs.service.fs.GroupAgentStrategyService;
@Service
public class GroupAgentStrategyServiceImpl extends ServiceImpl<GroupAgentStrategyMapper, GroupAgentStrategy> implements GroupAgentStrategyService{

}
