package cn.com.tzy.springbootfs.service.fs;

import cn.com.tzy.springbootentity.dome.fs.GroupOverflow;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupOverFlowInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface GroupOverflowService extends IService<GroupOverflow>{

    List<GroupOverFlowInfo> findGroupOverFlowInfo();

}
