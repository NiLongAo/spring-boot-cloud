package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.vo.video.StreamProxyVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StreamProxyMapper extends BaseMapper<StreamProxy> {
    int findPageCount(@Param("param") StreamProxyPageParam param);
    List<StreamProxyVo> findPageResult(@Param("param") StreamProxyPageParam param);

    StreamProxyVo detail(@Param("id") Long id);
}