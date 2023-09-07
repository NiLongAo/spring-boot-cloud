package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Map;


@NoArgsConstructor
public class BaseUserService implements UserDetailsService {
    /**
     * 不同类型用户登陆
     */
    public Map<String,UserDetailsService> userDetailsServiceMap;

    public BaseUserService(Map<String,UserDetailsService> userDetailsServiceMap){
        this.userDetailsServiceMap =userDetailsServiceMap;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String loginType = JwtUtils.getLoginType();
        if(StringUtils.isEmpty(loginType)){
            throw new RuntimeException("loginType is null");
        }
        UserDetailsService userDetailsService = userDetailsServiceMap.get(loginType);
        if(userDetailsService == null){
            throw new RuntimeException("userDetailsService not implements");
        }
        return userDetailsService.loadUserByUsername(username);
    }

}
