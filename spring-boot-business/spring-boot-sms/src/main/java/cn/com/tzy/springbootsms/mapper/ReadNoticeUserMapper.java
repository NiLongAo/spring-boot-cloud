package cn.com.tzy.springbootsms.mapper;

import cn.com.tzy.springbootentity.dome.sms.ReadNoticeUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReadNoticeUserMapper extends BaseMapper<ReadNoticeUser> {

    int findUserIdNoticeIdCount(@Param("userId") Long userId, @Param("noticeId") Long noticeId);

    List<ReadNoticeUser> findNoticeIdCount(@Param("noticeId") Long noticeId);
}