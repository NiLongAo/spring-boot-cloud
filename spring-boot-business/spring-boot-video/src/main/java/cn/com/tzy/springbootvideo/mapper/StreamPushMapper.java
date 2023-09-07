package cn.com.tzy.springbootvideo.mapper;

import cn.com.tzy.springbootentity.dome.video.StreamPush;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.vo.video.StreamPushVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StreamPushMapper extends BaseMapper<StreamPush> {
    List<StreamPush> findMediaServiceNotGbId(@Param("mediaServiceId") String mediaServiceId);

    int findPageCount( @Param("param") StreamPushPageParam param);

    List<StreamPushVo> findPageResult(@Param("param") StreamPushPageParam param);


    StreamPushVo detail(@Param("id") Long id);
}