package cn.com.tzy.springbootsms.mapper;

import cn.com.tzy.springbootentity.dome.sms.MobileMessage;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MobileMessageMapper extends BaseMapper<MobileMessage> {
    int findPageCount(MobileMessageParam page);

    List<MobileMessage> findPageResult(MobileMessageParam page);
}