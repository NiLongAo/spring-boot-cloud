import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class TestDatagramChannelSender {

    /**
     * DatagramChannel客户端
     * @param args
     */
    public static void main(String[] args) throws IOException {
        //获取未绑定的Channel
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            //发送数据
            String string = sc.nextLine();
            buffer.clear();
            buffer.put(string.getBytes());
            buffer.flip();
            datagramChannel.send(buffer, new InetSocketAddress("localhost", 8899));
            //接收数据
            buffer.clear();
            SocketAddress receive = datagramChannel.receive(buffer);
            while (receive == null){
                receive = datagramChannel.receive(buffer);
            }
            buffer.clear();
            System.out.println(new String(buffer.array(),0,buffer.limit()));
        }
    }
}
