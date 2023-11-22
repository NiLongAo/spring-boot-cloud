package cn.com.tzy.springbootentity.common.info;


import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.BaseUser;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户基本表
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityBaseUser implements BaseUser,Serializable {
    /**
    * 编号
    */
    private Long id;
    /**
     * 账号
     */
    private String userName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 图像
     */
    private String imageUrl;
    /**
     * 密码
     */
    private String password;
    /**
     * 加盐
     */
    private String credentialssalt;
    /**
     * 是否核心管理员 1是 0否
     */
    private Integer isAdmin;
    /**
     * 是否禁止登录 1是 0否
     */
    private Integer isEnabled;
    /**
     * 是否小程序认证
     */
    private Boolean isMaAuthentication = true;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 租户编号
     */
    private Integer tenantStatus;
    /**
     * 角色集合
     */
    private List<Long> roleIdList;
    /**
     * 职位集合
     */
    private List<Long> positionIdList;
    /**
     * 部门集合
     */
    private List<Long> departmentIdList;
    /**
     * 权限信息
     */
    private List<String> privilegeList;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setCredentialssalt(String credentialssalt) {
        this.credentialssalt = credentialssalt;
    }

    @Override
    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    @Override
    public void setPositionIdList(List<Long> positionIdList) {
        this.positionIdList = positionIdList;
    }

    @Override
    public void setDepartmentIdList(List<Long> departmentIdList) {
        this.departmentIdList = departmentIdList;
    }

    @Override
    public void setPrivilegeList(List<String> privilegeList) {
        this.privilegeList = privilegeList;
    }
    @Override
    public void setTenantId(Long tenantId){
        this.tenantId = tenantId;
    }
    @Override
    public void setTenantStatus(Integer tenantStatus){
        this.tenantStatus = tenantStatus;
    }
}
