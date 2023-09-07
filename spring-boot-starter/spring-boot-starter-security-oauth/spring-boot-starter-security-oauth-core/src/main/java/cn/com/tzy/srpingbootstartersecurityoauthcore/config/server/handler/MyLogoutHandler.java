package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyLogoutHandler implements LogoutSuccessHandler {

    private TokenStore tokenStore;

    public MyLogoutHandler(TokenStore tokenStore){
        this.tokenStore =tokenStore;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RestResult<?> result = new RestResult();
        try{
            String authorization = JwtUtils.getAuthorization();
            if(StringUtils.isEmpty(authorization)){
                result =RestResult.result(RespCode.CODE_2.getValue(),"未获取认证信息");
            }else {
                OAuth2AccessToken accessToken = tokenStore.readAccessToken(authorization);
                if (accessToken == null) {
                    result =RestResult.result(RespCode.CODE_2.getValue(),"解析认证信息失败");
                }else {
                    // 移除access_token
                    tokenStore.removeAccessToken(accessToken);
                    // 移除refresh_token
                    if (accessToken.getRefreshToken() != null) {
                        tokenStore.removeRefreshToken(accessToken.getRefreshToken());
                    }
                    result =RestResult.result(RespCode.CODE_0.getValue(),null);
                }
            }
        }catch (AuthenticationException exception){
            result.setCode(RespCode.CODE_2.getValue());
            result.setMessage(exception.getMessage());
        }
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(AppUtils.encodeJson2(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
