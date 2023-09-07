package cn.com.tzy.springbootstarternetty.md;

import cn.com.tzy.springbootstarternetty.msg.Message;
import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        message.encode(byteBuf);
        if(log.isDebugEnabled()) {
            byteBuf.markReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            log.debug("sent {} to hex: {}", channelHandlerContext.channel().remoteAddress(), new String(HexUtil.encodeHex(bytes)));
            byteBuf.resetReaderIndex();
        }
    }
}