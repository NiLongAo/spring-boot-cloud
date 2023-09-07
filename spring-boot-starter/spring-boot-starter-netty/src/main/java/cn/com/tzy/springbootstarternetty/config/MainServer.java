//package cn.com.tzy.springbootstarternetty.config;
//
//
//import cn.com.tzy.springbootstarternetty.biz.DefaultBizFactory;
//import cn.com.tzy.springbootstarternetty.config.server.NettyServer;
//import cn.com.tzy.springbootstarternetty.factory.BizFactory;
//import cn.com.tzy.springbootstarternetty.msg.Message;
//import cn.com.tzy.springbootstarternetty.msg.MessageFactory;
//import io.netty.channel.SimpleChannelInboundHandler;
//
//public class MainServer extends NettyServer {
//    BizFactory bizFactory = new DefaultBizFactory();
//    private AppConfig appConfig ;
//
//    public MainServer(AppConfig appConfig) {
//        super(appConfig.getNettyPort());
//        this.appConfig = appConfig;
//        appConfig.setMainServer(this);
//    }
//
//    @Override
//    protected SimpleChannelInboundHandler<Message> newNettyHandler() {
//        return new MainServerHandler(bizFactory, appConfig.getExecutorService());
//    }
//
//    @Override
//    protected MessageFactory newMessageFactory() {
//        return MessageFactory.DEFAULT_INSTANCE;
//    }
//
//}