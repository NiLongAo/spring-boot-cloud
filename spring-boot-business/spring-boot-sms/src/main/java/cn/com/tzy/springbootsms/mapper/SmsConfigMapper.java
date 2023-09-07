package cn.com.tzy.springbootsms.mapper;

import cn.com.tzy.springbootentity.dome.sms.SmsConfig;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmsConfigMapper extends BaseMapper<SmsConfig> {

    int findPageCount(SmsConfigParam page);

    List<SmsConfig> findPageResult(SmsConfigParam page);

    List<SmsConfig> findList(@Param("isActive") Integer isActive, @Param("type") Integer type);
}