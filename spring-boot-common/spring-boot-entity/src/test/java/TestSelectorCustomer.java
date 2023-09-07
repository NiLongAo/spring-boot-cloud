import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class TestSelectorCustomer {
    /**
     * 选择器客户端
     * @param args
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //打开通道
        SocketChannel sc = SocketChannel.open();
        //设置非阻塞
        sc.configureBlocking(false);
        //链接服务器
        sc.connect(new InetSocketAddress(1234));
        //调用finishConnect()方法完成链接
        while (!sc.finishConnect()){
            Thread.sleep(2000);
            System.out.println("正在建立链接请稍等...");
        }
        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("输入要发送的内容：");
            String text = scanner.nextLine();
            ByteBuffer bb = ByteBuffer.allocate(text.length());
            bb.put(text.getBytes());
            bb.flip();
            while (bb.hasRemaining()){
                sc.write(bb);
            }
            bb.clear();
        }
    }
}
