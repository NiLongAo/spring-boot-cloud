import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TestSelectorSever {

    public static final int PORT =1234;
    private  static ByteBuffer buffer = ByteBuffer.allocate(10);

    /**
     * 选择器服务端
     * @param args
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //打开一个未绑定的ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //获得ServerSocketChannel关联的ServerSocket并绑定指定的端口
        ssc.socket().bind(new InetSocketAddress(PORT));
        //设置ServerSocketChannel为非阻塞模式
        ssc.configureBlocking(false);
        //创建选择器
        Selector selector = Selector.open();
        //将ServerSocketChannel注册到选择器上
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            //调用select()方法,会等待就绪的通道，会发生阻塞
            int readNum = selector.select();//返回就绪通道的数量
            if(readNum == 0){
                Thread.sleep(2000);
                System.out.println("通道未就绪");
                continue;
            }
            //获得选择键集合的迭代器
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                //判断通道是否有可接收的链接
                if(key.isAcceptable()){//接收就绪
                    //接收链接，获得ServerSocketChannel
                    //只有ServerSocketChannel支持ACCEPT时间
                    ServerSocketChannel sscTmp = (ServerSocketChannel) key.channel();
                    //调用accept()方法返回到达服务器的新客户端链接
                    SocketChannel socket = sscTmp.accept();
                    socket.configureBlocking(false);
                    socket.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                }else if(key.isReadable()){//可读就绪
                    //就绪通道有数据可读
                    readDateFromSocket(key);
                }else if(key.isWritable()){//可写
                    //写数据
                }
                //把selectionKey从已选择集合中删除
                it.remove();
            }
            Thread.sleep(1000);
        }
    }
    //读取指定通道上的数据
    private static void readDateFromSocket(SelectionKey key) throws IOException {
        //获取SocketChannel
        SocketChannel channel = (SocketChannel) key.channel();
        buffer.clear();
        List<Byte> list = new ArrayList<>();//定义集合存储读取到的字节
        int read = channel.read(buffer);
        while (read > 0){
            buffer.flip();
            while (buffer.hasRemaining()){
                list.add(buffer.get());
            }
            buffer.clear();
            read = channel.read(buffer);
        }
        //创建一个字节数组
        byte[] bytes = new byte[list.size()];
        //把list中的字节存储到字节数组中
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = list.get(i);
        }
        //生成字符串
        String string = new String(bytes).trim();
        System.out.println("服务器收到客户端的数据："+string);
        if(read<0){
            channel.close();
        }
    }


}
