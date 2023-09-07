package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectPosition;
import cn.com.tzy.springbootentity.param.bean.UserConnectPositionParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserConnectPositionService extends IService<UserConnectPosition>{

    RestResult<?> find(Long userId);

    RestResult<?> save(UserConnectPositionParam save);

}
