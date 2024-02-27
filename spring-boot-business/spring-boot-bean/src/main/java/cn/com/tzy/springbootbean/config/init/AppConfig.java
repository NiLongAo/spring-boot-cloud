package cn.com.tzy.springbootbean.config.init;

import cn.com.tzy.springbootbean.service.api.AreaService;
import cn.com.tzy.springbootentity.common.info.AreaInfo;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

//Constructor >> @Autowired >> PostConstruct >InitializingBean > ApplicationRunner
@Component
@Order(1)
public class AppConfig{

    @Resource
    private ConfigInit configInit;
    /**
     * 所有系统配置
     */
    private Map<String,String> allConfig;


    @SneakyThrows
    @PostConstruct
    public void init() {
        configInit.init(this);
    }

    /**
     * 获取minio服务地址
     * @param path
     * @return
     */
    public String findStaticPath(String path){
        if(StringUtils.isEmpty(path)){
            return "";
        }
        return String.format("%s%s", allConfig.get(ConstEnum.ConfigEnum.STATIC_PATH.getValue()),path);
    }




    public void setAllConfig(Map<String, String> allConfig) {
        this.allConfig = allConfig;
    }

}
