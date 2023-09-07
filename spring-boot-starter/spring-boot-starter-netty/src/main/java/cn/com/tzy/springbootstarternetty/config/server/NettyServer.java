package cn.com.tzy.springbootstarternetty.config.server;


import cn.com.tzy.springbootstarternetty.md.MessageDecoder;
import cn.com.tzy.springbootstarternetty.md.MessageEncoder;
import cn.com.tzy.springbootstarternetty.msg.Message;
import cn.com.tzy.springbootstarternetty.msg.MessageFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public abstract class NettyServer {

    int port;

    Channel channel;
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public NettyServer(int port) {
        this.port = port;
    }

    public void startup() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ReadTimeoutHandler(60 * 3));
                    ch.pipeline().addLast(new MessageDecoder(newMessageFactory()));
                    ch.pipeline().addLast(new MessageEncoder());
                    ch.pipeline().addLast(newNettyHandler());
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.TCP_NODELAY, true);
        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync();
        channel = f.channel();
    }

    public void close() {
        channel.close();

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    protected abstract SimpleChannelInboundHandler<Message> newNettyHandler();

    protected abstract MessageFactory newMessageFactory();
}