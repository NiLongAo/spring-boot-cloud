import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.springbootstartersocketio.properties.SocketIoProperties;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;

import java.util.List;
import java.util.stream.Collectors;

public class ChatLauncher1 {


    public static void main(String[] args) throws InterruptedException {
        SocketIoProperties socketIoProperties = new SocketIoProperties();
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        //自定义订阅发布工厂
        //config.setStoreFactory(jedisStoreFactory);
        config.setSocketConfig(socketConfig);
        //不指定ip 自动获取当前服务  nacos 注册时 获取当前服务注册的ip地址注册
        //config.setHostname(socketIoProperties.getHost());
        config.setPort(9092);

        config.setBossThreads(socketIoProperties.getBossCount());
        config.setWorkerThreads(socketIoProperties.getWorkCount());
        config.setAllowCustomRequests(socketIoProperties.getAllowCustomRequests());

        config.setUpgradeTimeout(socketIoProperties.getUpgradeTimeout());
        config.setPingInterval(socketIoProperties.getPingInterval());
        config.setPingTimeout(socketIoProperties.getPingTimeout());

        config.setMaxFramePayloadLength(socketIoProperties.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(socketIoProperties.getMaxHttpContentLength());

        SocketIOServer server = new SocketIOServer(config);

        NamespaceListener namespaceListener = new NamespaceListener() {
            @Override
            public String getNamespaceName() {
                return "/chat1";
            }

            @Override
            public SocketIOServer getSocketIOServer() {
                return server;
            }

            @Override
            public void setSocketIOServer(SocketIOServer socketIOServer) {

            }

            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("链接");
            }

            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("断开");
            }
        };
        EventListener<ChatObject> chatObjectEventListener = new EventListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackSender) throws Exception {
                client.sendEvent(getEventName(), data);
            }

            @Override
            public Class<ChatObject> getEventClass() {
                return ChatObject.class;
            }

            @Override
            public String getEventName() {
                return "message";
            }

            @Override
            public NamespaceListener getNamespace() {
                return namespaceListener;
            }
        };
        SocketIONamespace socketIONamespace = server.addNamespace(chatObjectEventListener.getNamespace().getNamespaceName());
        socketIONamespace.addConnectListener(chatObjectEventListener.getNamespace());
        socketIONamespace.addDisconnectListener(chatObjectEventListener.getNamespace());
        socketIONamespace.addEventListener(chatObjectEventListener.getEventName(), chatObjectEventListener.getEventClass(),chatObjectEventListener);

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
