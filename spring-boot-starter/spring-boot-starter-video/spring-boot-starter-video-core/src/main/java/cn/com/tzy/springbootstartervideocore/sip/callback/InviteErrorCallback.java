package cn.com.tzy.springbootstartervideocore.sip.callback;

public interface InviteErrorCallback<T> {

    void run(int code, String msg, T data);
}