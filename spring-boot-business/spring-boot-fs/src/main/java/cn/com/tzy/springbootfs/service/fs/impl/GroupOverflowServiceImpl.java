package cn.com.tzy.springbootfs.service.fs.impl;

import cn.com.tzy.springbootentity.dome.fs.GroupOverflow;
import cn.com.tzy.springbootfs.mapper.fs.GroupOverflowMapper;
import cn.com.tzy.springbootfs.service.fs.GroupOverflowService;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupOverFlowInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GroupOverflowServiceImpl extends ServiceImpl<GroupOverflowMapper, GroupOverflow> implements GroupOverflowService{

    @Override
    public List<GroupOverFlowInfo> findGroupOverFlowInfo() {
        return baseMapper.findGroupOverFlowInfo();
    }
}
