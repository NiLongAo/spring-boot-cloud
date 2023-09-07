package cn.com.tzy.springbootstarternetty.config.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class NettyClient implements Runnable {
    String host;
    int port;
    int nThreads;
    String threadName;

    public Notice offlineNotice;
    public Notice connectedNotice;

    volatile Channel channel;
    volatile boolean close = false;

    public NettyClient(String host, int port, int nThreads, String threadName) {
        this.host = host;
        this.port = port;
        this.nThreads = nThreads;
        this.threadName = threadName;
    }

    public NettyClient(String host, int port, int nThreads, String threadName, Notice offlineNotice, Notice connectedNotice) {
        this.host = host;
        this.port = port;
        this.nThreads = nThreads;
        this.threadName = threadName;
        this.offlineNotice = offlineNotice;
        this.connectedNotice = connectedNotice;
    }

    public void startup() throws InterruptedException {
        Thread thread = new Thread(this);
        thread.setName(threadName);
        thread.start();
    }

    public void run() {
        while (!close) {
            runServer();
        }
    }

    public void runServer() {
        EventLoopGroup group = new NioEventLoopGroup(nThreads);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            NettyClient.this.initChannel(ch);
                        }
                    });

            channel = b.connect(host, port).sync().channel();
            if(log.isDebugEnabled()) {
                log.debug("channel create success");
            }
            if(connectedNotice != null) {
                connectedNotice.notice(this);
            }
            channel.closeFuture().sync();
            if(log.isDebugEnabled()) {
                log.debug("channel lose connection");
            }

            if(offlineNotice != null) {
                offlineNotice.notice(this);
            }
        } catch (Exception e) {
            log.warn("connect fail", e);
            sleep();
        } finally {
            if(channel != null) {
                channel.close();
            }
            channel = null;
            group.shutdownGracefully();
        }
    }

    public void close() {
        close = true;
        if(channel != null) {
            channel.close();
            channel = null;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    protected abstract void initChannel(SocketChannel ch);

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            close = true;
        }
    }

    public interface Notice {
        void notice(NettyClient client);
    }
}