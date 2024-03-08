package cn.com.tzy.srpingbootstartersecurityoauthbasic.dome;


import java.util.List;

/**
 * 用户基本表
 */
public  interface BaseUser{

    /**
     * 编号
     */
    public Long getId();

    public void setId(Long id);

    /**
     * 编号
     */
    public String getLoginType();

    public void setLoginType(String loginType);
    /**
     * 编号
     */
    public String getPhone();

    public void sePhone(String phone);

    /**
     * 账号
     */
    public String getUserName();


    public void setUserName(String userName);
    /**
     * 图像
     */
    public String getImageUrl();


    public void setImageUrl(String imageUrl);

    /**
     * 密码
     */
    public String getPassword();

    public void setPassword(String password);

    /**
     * 加盐
     */
    public String getCredentialssalt();

    public void setCredentialssalt(String credentialssalt);

    /**
     * 是否核心管理员 1是 0否
     */
    public Integer getIsAdmin();

    public void setIsAdmin(Integer isAdmin);

    /**
     * 是否禁止登录 1是 0否
     */
    public Integer getIsEnabled();

    public void setIsEnabled(Integer isEnabled);

    /**
     * 权限信息
     */
    public List<String> getPrivilegeList();

    public void setPrivilegeList(List<String> privilegeList);


    /**
     * 角色集合
     */
    public List<Long> getRoleIdList();

    public void setRoleIdList(List<Long> roleIdList);
    /**
     * 职位集合
     */
    public List<Long> getPositionIdList();

    public void setPositionIdList(List<Long> positionIdList);

    /**
     * 部门集合
     */
    public List<Long> getDepartmentIdList();

    public void setDepartmentIdList(List<Long> departmentIdList);


    public Long getTenantId();
    public void setTenantId(Long tenantId);

    public Integer getTenantStatus();
    public void setTenantStatus(Integer tenantStatus);

}
