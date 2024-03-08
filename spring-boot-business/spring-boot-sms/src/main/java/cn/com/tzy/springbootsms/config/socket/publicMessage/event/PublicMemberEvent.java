package cn.com.tzy.springbootsms.config.socket.publicMessage.event;

import cn.com.tzy.springbootsms.config.socket.publicMessage.namespace.PublicMemberNamespace;
import cn.com.tzy.springbootstartersocketio.message.Message;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class PublicMemberEvent implements EventListener<Message> {

    public static final String PUBLIC_MEMBER_EVENT = "public_member_event";

    private final PublicMemberNamespace publicMemberNamespace;

    public PublicMemberEvent(PublicMemberNamespace publicMemberNamespace) {
        this.publicMemberNamespace = publicMemberNamespace;
    }

    @Override
    public Class<Message> getEventClass() {
        return Message.class;
    }

    @Override
    public String getEventName() {
        return PUBLIC_MEMBER_EVENT;
    }


    @Override
    public NamespaceListener getNamespace() {
        return publicMemberNamespace;
    }

    /**
     * 自定义消息事件，客户端js触发：socket.emit('messageevent', {msgContent: msg}); 时触发
     * 前端js的 socket.emit("事件名","参数数据")方法，是触发后端自定义消息事件的时候使用的,
     * 前端js的 socket.on("事件名",匿名函数(服务器向客户端发送的数据))为监听服务器端的事件
     * @param client　客户端信息
     * @param request 请求信息
     */
    @Override
    public void onData(SocketIOClient client, Message message, AckRequest request) {
        //socket io发送
        client.sendEvent(PUBLIC_MEMBER_EVENT, message);
    }


    public void sendList(List<String> roomIdList, Message message){
        roomIdList.forEach(obj -> publicMemberNamespace.getNamespace().getRoomOperations(obj).sendEvent(PUBLIC_MEMBER_EVENT,message));
    }
}
