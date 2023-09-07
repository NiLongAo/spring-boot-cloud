package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.OauthClientMapper;
import cn.com.tzy.springbootbean.service.api.OauthClientService;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.OauthClient;
import cn.com.tzy.springbootentity.param.sys.OauthClientParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OauthClientServiceImpl extends ServiceImpl<OauthClientMapper, OauthClient> implements OauthClientService{

    @Override
    public PageResult page(OauthClientParam param) {
        int total = baseMapper.findPageCount(param);
        List<OauthClient> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putString("clientId", obj.getClientId());
            map.putString("resourceIds", obj.getResourceIds());
            map.putString("clientSecret", obj.getClientSecret());
            map.putString("scope", obj.getScope());
            map.putString("authorizedGrantTypes", obj.getAuthorizedGrantTypes());
            map.putString("webServerRedirectUri", obj.getWebServerRedirectUri());
            map.putString("authorities", obj.getAuthorities());
            map.putInteger("accessTokenValidity", obj.getAccessTokenValidity());
            map.putInteger("refreshTokenValidity", obj.getRefreshTokenValidity());
            map.putString("additionalInformation", obj.getAdditionalInformation());
            map.putString("autoapprove", obj.getAutoapprove());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> detail(String clientId) {
        OauthClient client = baseMapper.selectOne(new QueryWrapper<OauthClient>().eq("client_id", clientId));
        if(client == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到客户端信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,client);
    }


    @Override
    public RestResult<?> save(String clientId, String resourceIds, String clientSecret, String scope, String authorizedGrantTypes, String webServerRedirectUri, String authorities, Integer accessTokenValidity, Integer refreshTokenValidity, String additionalInformation, String autoapprove) {
        OauthClient entity = null;
        if(clientId != null){
            entity = baseMapper.selectOne(new QueryWrapper<OauthClient>().eq("client_id", clientId));
            if(entity == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到客户端信息");
            }
        }else {
            entity = new OauthClient();
        }
        entity.setClientId(clientId);
        entity.setResourceIds(resourceIds);
        entity.setClientSecret(clientSecret);
        entity.setScope(scope);
        entity.setAuthorizedGrantTypes(authorizedGrantTypes);
        entity.setWebServerRedirectUri(webServerRedirectUri);
        entity.setAuthorities(authorities);
        entity.setAccessTokenValidity(accessTokenValidity);
        entity.setRefreshTokenValidity(refreshTokenValidity);
        entity.setAdditionalInformation(additionalInformation);
        entity.setAutoapprove(autoapprove);
        boolean b = super.saveOrUpdate(entity);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }
}
