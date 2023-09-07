package cn.com.tzy.springbootentity.vo.bean;

import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TenantUserVo {

    /**
     * 租户信息
     */
    @NotNull(message = "未获取租户信息")
    private TenantParam tenant;
    /**
     * 用户信息
     */
    @NotNull(message = "未获取用户信息")
    private UserParam user;

}
