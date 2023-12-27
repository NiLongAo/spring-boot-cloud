package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import cn.com.tzy.springbootfeignvideo.api.video.ParentPlatformServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 级联平台管理(上级平台)
 */
@Service
public class ParentPlatformService {

    @Resource
    private ParentPlatformServiceFeign parentPlatformServiceFeign;


    /**
     * 获取注册到本服务的所有sip服务
     */
    public RestResult<?> findSipList(){
        return parentPlatformServiceFeign.findSipList();
    }

    /**
     * 分页
     */
    public PageResult page(ParentPlatformPageParam param){
        return parentPlatformServiceFeign.page(param);
    }
    /**
     * 新增
     */
    public RestResult<?> insert(ParentPlatform param){
        return parentPlatformServiceFeign.insert(param);
    }
    /**
     * 修改
     */
    public RestResult<?> update(ParentPlatform param){
        return parentPlatformServiceFeign.update(param);
    }
    /**
     * 删除
     * @return
     */
    public RestResult<?> delete(Long id){
        return parentPlatformServiceFeign.delete(id);
    }

    public RestResult<?> detail(Long id) {
        return parentPlatformServiceFeign.detail(id);
    }
}
