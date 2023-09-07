package cn.com.tzy.springbootsms.mapper;

import cn.com.tzy.springbootentity.dome.sms.MobileMessageTemplate;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MobileMessageTemplateMapper extends BaseMapper<MobileMessageTemplate> {
    int findPageCount(MobileMessageTemplateParam page);

    List<MobileMessageTemplate> findPageResult(MobileMessageTemplateParam page);

    MobileMessageTemplate findLast(@Param("configId") Integer configId, @Param("type") Integer type);
}