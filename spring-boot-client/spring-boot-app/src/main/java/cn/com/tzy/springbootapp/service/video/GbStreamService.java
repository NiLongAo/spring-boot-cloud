package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import cn.com.tzy.springbootfeignvideo.api.video.GbStreamServiceFeign;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * 国标流相关接口
 */
@Service
public class GbStreamService{
    @Resource
    private GbStreamServiceFeign gbStreamServiceFeign;

    /**
     * 分页
     */
    public PageResult page(@Validated @RequestBody GbStreamPageParam param){
        return gbStreamServiceFeign.page(param);
    }

}
