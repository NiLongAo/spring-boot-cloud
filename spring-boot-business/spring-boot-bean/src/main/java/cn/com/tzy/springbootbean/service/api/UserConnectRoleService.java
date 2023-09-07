package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectRole;
import cn.com.tzy.springbootentity.param.bean.UserConnectRoleParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserConnectRoleService extends IService<UserConnectRole>{

    RestResult<?> find(Long userId);

    RestResult<?> save(UserConnectRoleParam param);


}
