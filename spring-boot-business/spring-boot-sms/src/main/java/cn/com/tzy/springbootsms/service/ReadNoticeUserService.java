package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootentity.dome.sms.ReadNoticeUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReadNoticeUserService extends IService<ReadNoticeUser>{

    public int findUserIdNoticeIdCount(Long userId,Long noticeId);

    public List<Long> findNoticeIdCount(Long noticeId);
}
