package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.SkillMapper;
import cn.com.tzy.springbootentity.dome.fs.Skill;
import cn.com.tzy.springbootfs.service.fs.SkillService;
@Service
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService{

}
