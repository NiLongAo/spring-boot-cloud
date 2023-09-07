package cn.com.tzy.springbootgateway.predicate;

import cn.com.tzy.springbootgateway.dome.TokenConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * token拦截器
 * 用法在配置中添加 -token= *****  值进行拦截
 */
@Log4j2
@Component
public class TokenRoutePredicateFactory extends AbstractRoutePredicateFactory<TokenConfig> {

    public TokenRoutePredicateFactory(){
        super(TokenConfig.class);
    }

    /**
     * 获取token值
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder (){
        return Collections.singletonList("token");
    }

    /**
     * 练习的
     * 判断token拦截是否一致
     * @param token
     * @return
     */
    @Override
    public Predicate<ServerWebExchange> apply(TokenConfig token) {
        return exchange->{
            MultiValueMap<String, String> valeMap = exchange.getRequest().getQueryParams();
            boolean flag =false;
            List<String> list = new ArrayList<>();
            valeMap.forEach((k,v)->{
                list.addAll(v);
            });
            for (String s:list){
                log.info("Token -> {}",s);
                if(StringUtils.equalsIgnoreCase(s,token.getToken())){
                    flag =true;
                    break;
                }
            }
            return flag;
        };
    }
}
