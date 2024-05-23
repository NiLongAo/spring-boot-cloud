package cn.com.tzy.springbootfs.mapper.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupOverflow;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupOverFlowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupOverflowMapper extends BaseMapper<GroupOverflow> {
    List<GroupOverFlowInfo> findGroupOverFlowInfo();
}