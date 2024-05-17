package cn.com.tzy.springbootfs.service.media;

import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootstarterfreeswitch.service.media.TokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TokenServiceImpl implements TokenService {
    @Resource
    private OAuthUserServiceFeign oAuthUserServiceFeign;
    @Override
    public boolean authentication(String token) {
//        RestResult<?> restResult = oAuthUserServiceFeign.checkToken(token);
//        if(restResult.getCode() == RespCode.CODE_0.getValue()){
//            Map<String, Object> map = BeanUtil.beanToMap(restResult.getData());
//            return MapUtil.getBool(map,"active",false);
//        }
//        return false;
        return true;
    }
}
