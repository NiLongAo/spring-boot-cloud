package cn.com.tzy.springbootvideo.config.authentication;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootstartervideocore.service.authentication.TokenService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class TokenServiceImpl implements TokenService {
    @Resource
    private OAuthUserServiceFeign oAuthUserServiceFeign;
    @Override
    public boolean authentication(String token) {
        RestResult<?> restResult = oAuthUserServiceFeign.checkToken(token);
        if(restResult.getCode() == RespCode.CODE_0.getValue()){
            Map<String, Object> map = BeanUtil.beanToMap(restResult.getData());
            return MapUtil.getBool(map,"active",false);
        }
        return false;
    }
}
