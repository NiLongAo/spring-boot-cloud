package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootfeignvideo.api.video.PlatformGbChannelServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 上级平台国标通道相关接口
 */
@Service
public class PlatformGbChannelService {

    @Resource
    private PlatformGbChannelServiceFeign platformGbChannelServiceFeign;


    /**
     * 分页（上级平台通道分页）
     */
    public RestResult<?> findDeviceChannelList(){
        return platformGbChannelServiceFeign.findDeviceChannelList();
    }

    /**
     * 国标级联绑定的通道key集合
     * @param param
     * @return
     */
    public RestResult<?> findChannelBindKey(PlatformGbChannelParam param){
        return platformGbChannelServiceFeign.findChannelBindKey(param);
    }

    /**
     * 向上级平台添加国标通道
     * @param param
     * @return
     */
    public RestResult<?> insert(PlatformGbChannelSaveParam param){
        return platformGbChannelServiceFeign.insert(param);
    }


    /**
     * 从上级平台移除国标通道
     * @param param
     * @return
     */
    public RestResult<?> delete(PlatformGbChannelSaveParam param){
        return platformGbChannelServiceFeign.delete(param);
    }

}
