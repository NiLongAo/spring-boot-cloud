package cn.com.tzy.springbootsms.config.socket.qr.namespace;

import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.namespace.Namespace;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class QRNamespace implements NamespaceListener {
    /**
     * 空间名称
     */
    public static final String QR_NAMESPACE = "/qr_namespace";

    private SocketIOServer socketIOServer;

    @Override
    public String getNamespaceName() {
        return QR_NAMESPACE;
    }

    /**
     * 客户端连接的时候触发，前端js触发：socket = io.connect("http://192.168.9.209:9092");
     * @param client
     */
    @Override
    public void onConnect(SocketIOClient client) {
        client.joinRoom(client.getSessionId().toString().replaceAll("-", ""));
        log.info("客户端:" + client.getSessionId() + "已连接");
    }

    /**
     * 客户端关闭连接时触发：前端js触发：socket.disconnect();
     * @param client
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        client.leaveRoom(client.getSessionId().toString().replaceAll("-", ""));
        log.info("客户端:" + client.getSessionId() + "断开连接");
    }

    public Namespace getNamespace(){
        return  (Namespace)socketIOServer.getNamespace(QR_NAMESPACE);
    }

    @Override
    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    @Override
    public void setSocketIOServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }
}
