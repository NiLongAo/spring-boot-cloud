package cn.com.tzy.springbootsms.config.init;

import cn.com.tzy.springbootstartersocketio.properties.SocketIoProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 将socket地址注册到nacos
 * Constructor >> @Autowired >> @PostConstruct  >> InitializingBean >> ApplicationRunner
 */
@Component
@Log4j2
public class SocketNacosApplicationRunner implements ApplicationRunner {

    @Autowired
    private AppConfig config;
    @Autowired
    private SocketIoProperties socketIoProperties;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Autowired
    private NacosServiceManager nacosServiceManager;

    public SocketNacosApplicationRunner(AppConfig config) {
        this.config = config;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //服务注册成功后手动添加到nacos中
        NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        namingService.registerInstance(socketIoProperties.getName(),nacosDiscoveryProperties.getIp(),socketIoProperties.getPort());
        config.setSocketNacosApplicationRunner(this);
        log.info("nacos-socket 成功注册nacos ip:{}:{}",nacosDiscoveryProperties.getIp(),socketIoProperties.getPort());
    }

    public void destroy() throws NacosException {
        //销毁nacos-socket注册nacos
        NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        namingService.deregisterInstance(socketIoProperties.getName(),nacosDiscoveryProperties.getIp(),socketIoProperties.getPort());
        log.info("nacos-socket 成功销毁注册nacos");
    }



}
