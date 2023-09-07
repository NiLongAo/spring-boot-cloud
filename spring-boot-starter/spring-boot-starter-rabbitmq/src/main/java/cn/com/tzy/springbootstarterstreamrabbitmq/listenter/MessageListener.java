package cn.com.tzy.springbootstarterstreamrabbitmq.listenter;

import com.rabbitmq.client.Channel;

public interface MessageListener<T> {


    /**
     * 接收消息
     * @param message 消息集合
     * @param channel 通道
     */
    public void  onMessage(T message, Channel channel);

}
