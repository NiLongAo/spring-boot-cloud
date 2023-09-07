package cn.com.tzy.springbootstarternetty.config;//package cn.com.lampblack.netty.cope.init;
//
//import cn.com.lampblack.netty.cope.biz.DefaultBizFactory;
//import cn.com.lampblack.netty.cope.factory.BizFactory;
//import cn.com.lampblack.netty.cope.init.client.NettyClient;
//import cn.com.lampblack.netty.cope.init.handler.NettyHandler;
//import cn.com.lampblack.netty.cope.md.MessageDecoder;
//import cn.com.lampblack.netty.cope.md.MessageEncoder;
//import cn.com.lampblack.netty.cope.msg.MessageFactory;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.timeout.IdleStateEvent;
//import io.netty.handler.timeout.IdleStateHandler;
//
//import java.util.concurrent.ExecutorService;
//
//public class VirtualServerClient extends NettyClient {
//    BizFactory bizFactory = new DefaultBizFactory();
//    AppConfig config;
//
//    public VirtualServerClient(String host, int port, String threadName, AppConfig config) {
//        super(host, port, 1, threadName);
//        this.config = config;
//    }
//
//    @Override
//    protected void initChannel(SocketChannel ch) {
//        ch.pipeline().addLast(new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS));
//        ch.pipeline().addLast(new MessageDecoder(MessageFactory.DEFAULT_INSTANCE));
//        ch.pipeline().addLast(new MessageEncoder());
//        ch.pipeline().addLast(new ServiceServerClientHandler(bizFactory, config.executorService));
//    }
//
//    public static class ServiceServerClientHandler extends NettyHandler {
//        public ServiceServerClientHandler(BizFactory bizFactory, ExecutorService executorService) {
//            super(bizFactory, executorService);
//        }
//        @Override
//        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//            if (evt instanceof IdleStateEvent) {//心跳机制
//                Msg212000003 msg = new Msg212000003();
//                ctx.writeAndFlush(msg);
//            }
//        }
//    }
//}