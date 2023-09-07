package cn.com.tzy.springbootbean.config.init;

import cn.com.tzy.springbootentity.common.info.AreaInfo;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

//Constructor >> @Autowired >> PostConstruct >InitializingBean > ApplicationRunner
@Component
@Order(1)
public class AppConfig{

    @Autowired
    private ConfigInit configInit;
    @Autowired
    private AreaInit areaInit;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 所有系统配置
     */
    private Map<String,String> allConfig;

    /**
     * 所有地图信息
     */
    private Map<Integer,String> allAreaName;

    /**
     * 所有地图信息
     */
    private List<AreaInfo> allArea;


    @SneakyThrows
    @PostConstruct
    public void init() {
        areaInit.init(this);
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
        return String.format("%s%s", allConfig.get(ConstEnum.ConfigEnum.STATSE_PATH.getValue()),path);
    }

    public String findAddress(Integer provinceId,Integer cityId,Integer areaId){
        StringBuffer str = new StringBuffer();
        if(provinceId != null){
            str.append(allAreaName.get(provinceId));
        }
        if(cityId != null){
            str.append(allAreaName.get(cityId));
        }
        if(areaId != null){
            str.append(allAreaName.get(areaId));
        }
        return str.toString();
    };


    public void setAllConfig(Map<String, String> allConfig) {
        this.allConfig = allConfig;
    }


    public void setAllArea(List<AreaInfo> allArea) {
        this.allArea = allArea;
    }

    public List<AreaInfo> getAllArea() {
        return allArea;
    }

    public void setAllAreaName(Map<Integer, String> allAreaName) {
        this.allAreaName = allAreaName;
    }
}
