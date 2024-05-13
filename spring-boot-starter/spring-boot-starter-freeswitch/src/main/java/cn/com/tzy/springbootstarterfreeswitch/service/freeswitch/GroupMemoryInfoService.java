package cn.com.tzy.springbootstarterfreeswitch.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupMemoryInfo;

public interface GroupMemoryInfoService {

    /**
     * 查询分组中 坐席与客户记忆
     */
    GroupMemoryInfo find(String groupId,String phone);


}
