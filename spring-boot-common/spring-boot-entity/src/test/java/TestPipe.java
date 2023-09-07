import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class TestPipe {


    /**
     * 两个线程之间通过Pipe管道数据传输
     * 使用PipedOutputStream和PipedInputStream 两个类分别是管道输出流与管道输入流
     * 在管道通信时，线程A向PipedOutputStream中写入数据，这些数据会自动发送到对应的PipedInputStream
     * 线程B可以从PipedInputStream中读取数据
     * @param args
     */
    public static void main(String[] args) throws IOException {
        //创建输入流管道与输出流管道
        PipedInputStream in= new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        //在输入流与输出流之间建立链接管道
        in.connect(out);
        //out.connect(in);作用一样
        //创建线程
        Sender sender = new Sender(out);
        Receiver receiver = new Receiver(in);
        new Thread(sender).start();
        new Thread(receiver).start();
    }
}
//发送端
class Sender implements Runnable{
    PipedOutputStream out;
    public Sender(PipedOutputStream out){
        super();
        this.out = out;
    }
    @Override
    public void run() {
        //模拟发送数据
        try {
            for (int i = 0; i < 100; i++) {
                String text = "Hello ,sender :"+i+"\r\n";
                out.write(text.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
//接收端
class Receiver implements Runnable{
     PipedInputStream in;

    public Receiver(PipedInputStream in){
        super();
        this.in = in;
    }
    @Override
    public void run() {
        //接收收据
        byte[] bytes = new byte[1024];
        try {
            int len = in.read(bytes);
            while (len>0){
                System.out.println(new String(bytes,0,len));
                len = in.read(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
