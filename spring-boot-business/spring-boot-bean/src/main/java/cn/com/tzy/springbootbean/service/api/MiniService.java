package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MiniService extends IService<Mini>{

    /**
     * 根据web用户编号获取小程序绑定用户
     * @param userId
     * @return
     */
    RestResult<?> findWebUserId(Long userId);
    /**
     * 微信绑定web端用户
     * @param param
     * @return
     */
    RestResult<?> saveMiniUser(MiniUserParam param);

    RestResult<?> unbindMiniWeb(MiniUserParam param);
}
