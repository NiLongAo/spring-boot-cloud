package cn.com.tzy.springbootstarternetty.biz;

import cn.com.tzy.springbootstarternetty.msg.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public abstract class Biz {

    public abstract void doBiz(ChannelHandlerContext context, Map<String, Object> attributes, Object message) throws Exception;

    protected void writeAndFlush(final ChannelHandlerContext context, final Message message) {
        ChannelFuture future = context.writeAndFlush(message);
        if(log.isDebugEnabled()) {
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.debug("sent to {}, {}", context.channel().remoteAddress(), message);
                }
            });
        }
    }

    protected void writeAndClose(final ChannelHandlerContext context, final Message message) {
        ChannelFuture future = context.writeAndFlush(message);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(log.isDebugEnabled()) {
                    log.debug("sent to {}, {}", context.channel().remoteAddress(), message);
                }
                context.close();
                if(log.isDebugEnabled()) {
                    log.debug("invoke close()");
                }
            }
        });
    }
}
