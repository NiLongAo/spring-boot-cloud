import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class TestDatagramChannelReceive {

    /**
     * DatagramChannel模拟数据接受端
     * @param args
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //创建一个未绑定的通道
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(8899));
        Scanner sc = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true){
            //先接受收据
            buffer.clear();
            InetSocketAddress sa = (InetSocketAddress) datagramChannel.receive(buffer);
            //判断是否有接收到数据
            if(sa == null){
                Thread.sleep(1000);
                continue;
            }
            System.out.print("Data from:"+sa);
            buffer.flip();
            String mag = new String(buffer.array(),0,buffer.limit());
            System.out.println(" :"+sa.getPort()+"--->"+ mag);
            //发送数据
            String text = sc.nextLine();
            datagramChannel.send(ByteBuffer.wrap(text.getBytes()),sa);
        }
    }

}
