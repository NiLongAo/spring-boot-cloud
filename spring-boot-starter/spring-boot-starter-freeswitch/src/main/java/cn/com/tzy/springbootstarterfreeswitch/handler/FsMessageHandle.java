package cn.com.tzy.springbootstarterfreeswitch.handler;

import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;

/**
 * 发送相关消息接口
 */
public interface FsMessageHandle {

    void handler(MessageModel model);


}
