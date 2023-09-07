package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.OauthClient;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface OauthClientService extends IService<OauthClient>{

    PageResult page(OauthClientParam param);

    RestResult<?> detail(String clientId);

    RestResult<?> save(String clientId, String resourceIds, String clientSecret, String scope, String authorizedGrantTypes, String webServerRedirectUri, String authorities, Integer accessTokenValidity, Integer refreshTokenValidity, String additionalInformation, String autoapprove);

}
