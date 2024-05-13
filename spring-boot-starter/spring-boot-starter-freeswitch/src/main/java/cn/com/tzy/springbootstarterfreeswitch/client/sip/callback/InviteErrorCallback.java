package cn.com.tzy.springbootstarterfreeswitch.client.sip.callback;

public interface InviteErrorCallback<T> {

    void run(int code, String msg, T data);
}