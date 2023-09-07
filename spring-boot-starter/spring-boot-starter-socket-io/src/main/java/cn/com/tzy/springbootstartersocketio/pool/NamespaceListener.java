package cn.com.tzy.springbootstartersocketio.pool;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

public interface NamespaceListener extends ConnectListener, DisconnectListener {
    /**
     * 获取所属空间名
     * @return
     */
    public String getNamespaceName();


    public SocketIOServer getSocketIOServer();

    public void setSocketIOServer(SocketIOServer socketIOServer);

}
