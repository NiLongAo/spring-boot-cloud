package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.GroupStrategyExp;
import cn.com.tzy.springbootfs.mapper.fs.GroupStrategyExpMapper;
import cn.com.tzy.springbootfs.service.fs.GroupStrategyExpService;
@Service
public class GroupStrategyExpServiceImpl extends ServiceImpl<GroupStrategyExpMapper, GroupStrategyExp> implements GroupStrategyExpService{

}
