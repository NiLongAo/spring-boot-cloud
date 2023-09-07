import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestServer {

    /**
     * 服务器端代码
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        int port =1234; //ServerSocket的端口号
        //建立一个未绑定ServerSocket服务器的通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //ServerSocketChannel没有bind绑定方法，需要先通过socket()方法获取ServerSocket对象，在进行绑定端口号
        ssc.socket().bind(new InetSocketAddress(port));
        //设置通道未非阻塞模式通道，当没有传入链接时，accept()返回null
        ssc.configureBlocking(false);
        while (true){
            System.out.println("ServerSocket服务器已准备就绪");
            SocketChannel sc = ssc.accept();//接受接入的ServerChannel
            if(sc == null){
                Thread.sleep(2000);
            }else {
                //先给客户端发送一个问候
                ByteBuffer buf = ByteBuffer.allocate(1024);
                buf.put("hello, I an from socketServer".getBytes());
                buf.flip();
                sc.write(buf);
                // 在读取客户端中发送来的内容
                System.out.println("from Sockte Client :" +sc.socket());
                buf.clear();
                sc.read(buf); //读取客户端发送的书序保存到buffer中
                buf.flip();
                CharBuffer decode = Charset.defaultCharset().decode(buf);
                System.out.println(decode);
                sc.close();
            }
        }
    }
}
