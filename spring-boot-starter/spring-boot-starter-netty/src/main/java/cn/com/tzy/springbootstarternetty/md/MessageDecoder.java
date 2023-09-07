package cn.com.tzy.springbootstarternetty.md;

import cn.com.tzy.springbootstarternetty.msg.Message;
import cn.com.tzy.springbootstarternetty.msg.MessageFactory;
import cn.com.tzy.springbootstarternetty.msg.MsgCode;
import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MessageDecoder extends ByteToMessageDecoder {


    MessageFactory messageFactory;

    public MessageDecoder(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> list) throws Exception {
        if(log.isDebugEnabled()) {
            buf.markReaderIndex();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            log.debug("from {} recv hex: {}", context.channel().remoteAddress(), new String(HexUtil.encodeHex(bytes)));
            buf.resetReaderIndex();
        }
        buf.markReaderIndex();
        if(buf.readableBytes() >= 12) {
            int msgCode = buf.readInt(); //msgCode
            buf.readInt(); //searial number
            int length = buf.readInt();
            List<Integer> inCodeCrcList = MsgCode.getInCodeCrcList();
            //如果是加入crc认证 需提取
            if(!inCodeCrcList.isEmpty() && inCodeCrcList.contains(msgCode)){
                buf.readShort();
            }
            if(buf.readableBytes() >= length) { // ok
                Class<? extends Message> clazz = messageFactory.getClass(msgCode);
                if(clazz == null) {
                    if(log.isWarnEnabled()) {
                        log.warn("Not recognition message code: {}", msgCode);
                    }
                } else {
                    buf.resetReaderIndex();
                    Message message = clazz.newInstance();
                    message.decode(buf);
                    list.add(message);
                }
            } else {
                buf.resetReaderIndex();
            }
        } else {
            buf.resetReaderIndex();
        }
    }
}