package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.OauthClient;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OauthClientMapper extends BaseMapper<OauthClient> {

    int findPageCount(OauthClientParam param);

    List<OauthClient> findPageResult(OauthClientParam param);


}
