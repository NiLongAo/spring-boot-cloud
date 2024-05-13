package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.SkillAgentMapper;
import cn.com.tzy.springbootentity.dome.fs.SkillAgent;
import cn.com.tzy.springbootfs.service.fs.SkillAgentService;
@Service
public class SkillAgentServiceImpl extends ServiceImpl<SkillAgentMapper, SkillAgent> implements SkillAgentService{

}
