package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.handler;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.spring.SpringContextHolder;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.Common;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

@Log4j2
@Order(Ordered.LOWEST_PRECEDENCE-1000)
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    private final ClientDetailsService clientDetailsService;
    private final AuthorizationServerTokenServices tokenServices;

    public LoginSuccessHandler(ClientDetailsService clientDetailsService,AuthorizationServerTokenServices tokenServices){
        this.clientDetailsService = clientDetailsService;
        this.tokenServices = tokenServices;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        PasswordEncoder passwordEncoder = SpringContextHolder.getBean(PasswordEncoder.class);
        String client_id = httpServletRequest.getParameter(Common.SPRING_SECURITY_RESTFUL_CLIENT_ID_KEY);
        String client_secret = httpServletRequest.getParameter(Common.SPRING_SECURITY_RESTFUL_CLIENT_SECRET_KEY);
        RestResult<OAuth2AccessToken> result = new RestResult();
        boolean flas = true;
        ClientDetails clientDetails = null;
        try{
            clientDetails =  clientDetailsService.loadClientByClientId(client_id);
            if (clientDetails == null) {
                log.error("clientId对应的配置q信息不存在:{}",client_id);
                throw new UnapprovedClientAuthenticationException("clientId对应的配置q信息不存在");
            } else if (!passwordEncoder.matches(client_secret,clientDetails.getClientSecret())) {
                log.error("clientSecret不匹配:{}",client_secret);
                throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
            }
        }catch (AuthenticationException exception){
            result.setCode(RespCode.CODE_2.getValue());
            result.setMessage(exception.getMessage());
            flas =false;
        }
        if(flas){
            TokenRequest tokenRequest = new TokenRequest(new LinkedHashMap<>(), client_id, clientDetails.getScope(), "custom");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken token = tokenServices.createAccessToken(oAuth2Authentication);
            result.setCode(RespCode.CODE_0.getValue());
            result.setData(token);
            String jti= String.valueOf(token.getAdditionalInformation().get(Common.JWT_JTI));
            String userId= String.valueOf(token.getAdditionalInformation().get(Common.JWT_USER_ID));
            String clientId= String.valueOf(token.getAdditionalInformation().get(Common.SPRING_SECURITY_RESTFUL_CLIENT_ID_KEY));
            long time =  Constant.EXRP_DAY*7;
            if(clientDetails.getAccessTokenValiditySeconds() != null && clientDetails.getAccessTokenValiditySeconds() > 0){ //与客户端token时间保持一致
                time = clientDetails.getAccessTokenValiditySeconds();
            }
            String accessKey = String.format("%s_%s_%s",Constant.AUTH_TOKEN_ACCESS_PREFIX,clientId,userId);
            RedisUtils.set(accessKey,jti,time);
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(AppUtils.encodeJson2(result).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

//    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
}