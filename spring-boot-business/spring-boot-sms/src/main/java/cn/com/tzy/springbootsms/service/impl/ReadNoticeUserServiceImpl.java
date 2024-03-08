package cn.com.tzy.springbootsms.service.impl;

import cn.com.tzy.springbootsms.service.ReadNoticeUserService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.sms.ReadNoticeUser;
import cn.com.tzy.springbootsms.mapper.ReadNoticeUserMapper;

import java.util.List;

@Service
public class ReadNoticeUserServiceImpl extends ServiceImpl<ReadNoticeUserMapper, ReadNoticeUser> implements ReadNoticeUserService {

    @Override
    public int findUserIdNoticeIdCount(Long userId,Long noticeId){
        return  baseMapper.findUserIdNoticeIdCount(userId,noticeId);
    }

    @Override
    public List<ReadNoticeUser> findNoticeIdCount(Long noticeId){
        return  baseMapper.findNoticeIdCount(noticeId);
    }
}
