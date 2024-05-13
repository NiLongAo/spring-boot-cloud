package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.SkillGroupMapper;
import cn.com.tzy.springbootentity.dome.fs.SkillGroup;
import cn.com.tzy.springbootfs.service.fs.SkillGroupService;
@Service
public class SkillGroupServiceImpl extends ServiceImpl<SkillGroupMapper, SkillGroup> implements SkillGroupService{

}
