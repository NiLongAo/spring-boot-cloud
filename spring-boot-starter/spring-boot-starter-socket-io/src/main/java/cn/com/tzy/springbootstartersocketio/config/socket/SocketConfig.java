package cn.com.tzy.springbootstartersocketio.config.socket;

import cn.com.tzy.springbootstartersocketio.config.Jedis.JedisStoreFactory;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.springbootstartersocketio.properties.SocketIoProperties;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SocketIoProperties.class)
@Import({JedisStoreFactory.class})
public class SocketConfig<T> {

    private final SocketIoProperties socketIoProperties;

    @Autowired
    private JedisStoreFactory jedisStoreFactory;
    @Autowired
    private SocketIOServer socketIOServer;

    /**
     * netty-socketio服务器 之前
     */
    @Bean
    public SocketIOServer socketIOServer(ObjectProvider<EventListener<T> > eventListenerList) {
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        //自定义订阅发布工厂
        config.setStoreFactory(jedisStoreFactory);
        config.setSocketConfig(socketConfig);
        //不指定ip 自动获取当前服务  nacos 注册时 获取当前服务注册的ip地址注册
        //config.setHostname(socketIoProperties.getHost());
        config.setPort(socketIoProperties.getPort());

        config.setBossThreads(socketIoProperties.getBossCount());
        config.setWorkerThreads(socketIoProperties.getWorkCount()); 
        config.setAllowCustomRequests(socketIoProperties.getAllowCustomRequests());

        config.setUpgradeTimeout(socketIoProperties.getUpgradeTimeout());
        config.setPingInterval(socketIoProperties.getPingInterval());
        config.setPingTimeout(socketIoProperties.getPingTimeout());
        // TODO: 2022/11/9 问题记录 长度过长修改长度时 需要在gateway配置文件中一并修改复制gateway的netty拦截报错
        config.setMaxFramePayloadLength(socketIoProperties.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(socketIoProperties.getMaxHttpContentLength());

        // 鉴权管理 --> SpringBoot OAuth2.0 封装登录、刷新令牌接口
        // config.setAuthorizationListener( data -> {
        //     String authorization = data.getSingleUrlParam(Constant.AUTHORIZATION_KEY);
        //     if (StringUtils.isEmpty(authorization)) {
        //         return false;
        //     }
        //     OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
        //     return !ObjectUtils.isEmpty(oAuth2AccessToken);
        // });
        SocketIOServer socketIOServer = new SocketIOServer(config);
        List<EventListener<T>> collect = eventListenerList.stream().collect(Collectors.toList());
        //注册空间监听
        for (EventListener<T> eventListener : collect) {
            NamespaceListener namespace = eventListener.getNamespace();
            namespace.setSocketIOServer(socketIOServer);
            SocketIONamespace socketIONamespace = socketIOServer.getNamespace(namespace.getNamespaceName());
            if(socketIONamespace == null){
                socketIONamespace = socketIOServer.addNamespace(namespace.getNamespaceName());
                socketIONamespace.addConnectListener(namespace);
                socketIONamespace.addDisconnectListener(namespace);
            }
            socketIONamespace.addEventListener(eventListener.getEventName(),eventListener.getEventClass(),eventListener);
        }
        return socketIOServer;
    }

    /**
     * 用于扫描netty-socketio的注解，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketIOServer);
    }

    //服务启动开启
    @PostConstruct
    public void init() {
        socketIOServer.start();
        log.info("nacos-socket服务端启动成功！");
    }

    //服务关闭销毁
    @PreDestroy
    public void destroy() {
        socketIOServer.stop();
        log.warn("nacos-socket服务端销毁成功！");
    }
}
