package cn.com.tzy.srpingbootstartersecurityoauthbasic.dome;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 登录用户信息
 */
@Data
@NoArgsConstructor
public class OAuthUserDetails implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private String imageUrl;

    private Boolean isAdmin;

    private Boolean enabled;

    private String credentialssalt;
    /**
     * 登陆方式
     */
    private String loginType;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 租户状态
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
     * 权限集合
     */
    private Collection<SimpleGrantedAuthority> authorities;
    public OAuthUserDetails(BaseUser user) {
        this.setId(user.getId());
        this.setLoginType(user.getLoginType());
        this.setImageUrl(user.getImageUrl());
        this.setTenantId(user.getTenantId());
        this.setTenantStatus(user.getTenantStatus());
        this.setPassword(ConstEnum.PasswordEncoderTypeEnum.BCRYPT.getPrefix() + user.getPassword());
        this.setCredentialssalt(user.getCredentialssalt());
        this.setRoleIdList(user.getRoleIdList());
        this.setPositionIdList(user.getPositionIdList());
        this.setDepartmentIdList(user.getDepartmentIdList());
        this.setIsAdmin(user.getIsAdmin() != null && user.getIsAdmin() == ConstEnum.Flag.YES.getValue());
        this.setEnabled(user.getIsEnabled() != null && user.getIsEnabled() == ConstEnum.Flag.YES.getValue());
        if (user.getPrivilegeList() != null && !user.getPrivilegeList().isEmpty()) {
            authorities = new ArrayList<>();
            user.getPrivilegeList().forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
