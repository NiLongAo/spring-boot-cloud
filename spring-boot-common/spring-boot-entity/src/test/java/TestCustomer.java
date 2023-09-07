import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestCustomer {

    /**
     * 客户端
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";//ServerSocket的IP地址
        int port =1234;           //ServerSocket注册的端口号
        InetSocketAddress address = new InetSocketAddress(host, port);
        //创建一个未链接SocketChannle
        SocketChannel sc = SocketChannel.open();
        //创建与服务器的链接
        sc.connect(address);
        //TCP链接需要一定时间，两个链接建立需要进行包对话
        //调用finishConnect()方法完成链接过程，如果没有链接成功返回false
        while (!sc.finishConnect()){
            System.out.println("等待链接过程中。。。。。");
            Thread.sleep(2000);
        }
        System.out.println("链接成功");
        //向服务器发送消息
        ByteBuffer buffer = ByteBuffer.wrap("hello,I an from clientsocketChannle".getBytes());
        while (buffer.hasRemaining()){
            sc.write(buffer);
        }
        //获得服务器发送给客户端的消息
        InputStream inputStream = sc.socket().getInputStream();
        ReadableByteChannel newChannel = Channels.newChannel(inputStream);
        buffer.clear();
        newChannel.read(buffer);
        buffer.flip();
        CharBuffer decode = Charset.defaultCharset().decode(buffer);
        System.out.println(decode);
    }

}
