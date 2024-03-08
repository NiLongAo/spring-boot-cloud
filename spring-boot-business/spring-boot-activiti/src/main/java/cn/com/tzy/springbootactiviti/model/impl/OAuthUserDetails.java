package cn.com.tzy.springbootactiviti.model.impl;


import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


/**
 * 登录用户信息
 */
@Data
@NoArgsConstructor
public class OAuthUserDetails implements UserDetails {

    private Long id;

    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 账号
     */
    private String loginType;

    private String password;

    private String imageUrl;

    private Boolean enabled;

    private Boolean isAdmin;

    private Long tenantId;

    private String clientId;

    private String credentialssalt;

    private Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();;

    public OAuthUserDetails(SecurityBaseUser user) {
        this.setId(user.getId());
        this.setUsername(user.getUserName());
        this.setLoginType(user.getLoginType());
        this.setPhone(user.getPhone());
        this.setImageUrl(user.getImageUrl());
        this.setTenantId(user.getTenantId());
        this.setPassword(ConstEnum.PasswordEncoderTypeEnum.BCRYPT.getPrefix() + user.getPassword());
        this.setCredentialssalt(user.getCredentialssalt());
        this.setIsAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1);
        this.setEnabled(user.getIsEnabled() == null || user.getIsEnabled() == 0);
        if (user.getPrivilegeList() != null && !user.getPrivilegeList().isEmpty()) {
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
        return this.enabled;
    }
}
