package cn.com.tzy.springbootsso.config.oauth.service.server;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.OauthClient;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignbean.api.sys.OauthClientServiceFeign;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 接入平台
 */
@Log4j2
@Component
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    private OauthClientServiceFeign oauthClientServiceFeign;


    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        log.info("About to produce ClientDetails with client-id :{}",clientId);
        try {
            RestResult<?> restResult = oauthClientServiceFeign.detail(clientId);
            if (restResult.getCode()== RespCode.CODE_0.getValue()) {
                OauthClient client = AppUtils.decodeJson2(AppUtils.encodeJson(restResult.getData()),OauthClient.class);
                BaseClientDetails clientDetails = new BaseClientDetails(
                        client.getClientId(),
                        client.getResourceIds(),
                        client.getScope(),
                        client.getAuthorizedGrantTypes(),
                        client.getAuthorities(),
                        client.getWebServerRedirectUri()
                );
                clientDetails.setClientSecret(ConstEnum.PasswordEncoderTypeEnum.NOOP.getPrefix() + client.getClientSecret());
                clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
                clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
                return clientDetails;
            } else {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            }
        } catch (EmptyResultDataAccessException var4) {
            log.error("No client with requested id:{}",clientId);
            throw new NoSuchClientException("No client with requested id: " + clientId);
        } catch (IOException e) {
            log.error("Json转换错误：",e);
            throw new NoSuchClientException("Json转换错误: " + clientId);
        } catch (Exception e){
            log.error("服务器获取失败：",e);
            throw new NoSuchClientException("未知错误: " + e);
        }
    }
}
