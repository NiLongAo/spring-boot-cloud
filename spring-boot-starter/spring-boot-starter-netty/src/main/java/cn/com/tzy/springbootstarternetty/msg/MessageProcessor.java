package cn.com.tzy.springbootstarternetty.msg;

import cn.com.tzy.springbootstarternetty.biz.Biz;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class MessageProcessor implements Runnable {

    private long submitTime = 0;
    private long beginTime = 0;
    private long endTime = 0;
    private boolean executeSuccess = true;

    public ChannelHandlerContext channelHandlerContext;
    public Object message;
    public Biz biz;
    public Map<String, Object> attributes;

    public MessageProcessor() {
        submitTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        before();
        try {
            //3分钟前的数据不处理
            if(beginTime - submitTime > 3 * 60 * 1000){
                if(log.isInfoEnabled()) {
                    log.info("线程阻塞放弃");
                }
                return;
            }
            biz.doBiz(channelHandlerContext, attributes, message);
        } catch (Throwable e) {
            executeSuccess = false;
            log.error("MessageProcessor execute error", e);
            e.printStackTrace();
        } finally {
            after();
        }
    }

    protected void before() {
        beginTime = System.currentTimeMillis();
    }

    protected void after() {
        endTime = System.currentTimeMillis();
    }
}