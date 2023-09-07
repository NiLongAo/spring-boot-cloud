package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectDepartment;
import cn.com.tzy.springbootentity.param.bean.UserConnectDepartmentParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserConnectDepartmentService extends IService<UserConnectDepartment>{

    RestResult<?> find(Long userId);

    RestResult<?> save(UserConnectDepartmentParam save);

}
