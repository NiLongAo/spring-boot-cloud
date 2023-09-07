package cn.com.tzy.springbootstartersocketio.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haopeng
 */
@Data
@SuperBuilder(toBuilder = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "socket-io")
public class SocketIoProperties {

    /**
     * socket 服务名称
     */
    private String name = "socket-service";

    /**
     * host在本地测试可以设置为localhost或者本机IP，在Linux服务器跑可换成服务器IP
     */
    //private String host = "localhost";
    /**
     * socket 端口
     */
    private int port = 9092;
    /**
     * socket连接数大小（如只监听一个端口boss线程组为1即可）
     */
    private int bossCount = 1;

    /**
     * 工作数
     */
    private int workCount = 100;

    /**
     * 设置最大每帧处理数据的长度，防止他人利用大数据来攻击服务器
     */
    private int maxFramePayloadLength = 1024 * 1024;

    /**
     * 最大http内容长度限制
     */
    private int maxHttpContentLength = 1024 * 1024;

    /**
     * 允许服务自定义请求不同于 socket.io 协议。在这种情况下，有必要添加自己的处理程序来处理它们以避免挂起连接。默认为 false
     */
    private boolean allowCustomRequests = true;

    /**
     * 协议升级超时时间（毫秒），默认10秒。HTTP握手升级为ws协议超时时间
     */
    private int upgradeTimeout = 10 * 1000;

    /**
     *  Ping消息超时时间（毫秒），默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
     */
    private int pingTimeout = 30 * 1000;

    /**
     * Ping消息间隔（毫秒），默认25秒。客户端向服务器发送一条心跳消息间隔
     */
    private int pingInterval = 12 * 1000;

    public boolean getAllowCustomRequests() {
        return allowCustomRequests;
    }
}
