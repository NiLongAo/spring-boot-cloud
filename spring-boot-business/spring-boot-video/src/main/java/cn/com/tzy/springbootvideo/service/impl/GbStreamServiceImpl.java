package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import cn.com.tzy.springbootvideo.mapper.GbStreamMapper;
import cn.com.tzy.springbootvideo.service.GbStreamService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class GbStreamServiceImpl extends ServiceImpl<GbStreamMapper, GbStream> implements GbStreamService{


    @Override
    public GbStream findPlatformId(String platformId, String gbId) {
        return baseMapper.findPlatformId(platformId,gbId);
    }

    @Override
    public PageResult findPage(GbStreamPageParam param) {
        Page<GbStream> page = MyBatisUtils.buildPage(param);
        return MyBatisUtils.selectPage(baseMapper.findPage(page, param));
    }


}
