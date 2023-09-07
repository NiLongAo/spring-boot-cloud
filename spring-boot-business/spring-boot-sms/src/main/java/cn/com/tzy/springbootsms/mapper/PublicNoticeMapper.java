package cn.com.tzy.springbootsms.mapper;

import cn.com.tzy.springbootentity.dome.sms.MobileMessage;
import cn.com.tzy.springbootentity.dome.sms.PublicNotice;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PublicNoticeMapper extends BaseMapper<PublicNotice> {

    int findPageCount(PublicNoticeParam page);

    List<PublicNotice> findPageResult(PublicNoticeParam page);

    int findUserPageCount(PublicNoticeParam page);

    List<PublicNotice> findUserPageResult(PublicNoticeParam page);

    List<PublicNotice> findDateRange(@Param("date") Date date);
}