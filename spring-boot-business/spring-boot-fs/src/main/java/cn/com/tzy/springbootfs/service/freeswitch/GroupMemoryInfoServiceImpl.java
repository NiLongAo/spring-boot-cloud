package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupMemoryInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.GroupMemoryInfoService;
import org.springframework.stereotype.Service;

@Service
public class GroupMemoryInfoServiceImpl implements GroupMemoryInfoService {
    @Override
    public GroupMemoryInfo find(String groupId, String phone) {
        return null;
    }
}
