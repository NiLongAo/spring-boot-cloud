package cn.com.tzy.springbootstarternetty.msg;

public interface MessageFactory {

    public static MessageFactory DEFAULT_INSTANCE = new DefaultMessageFactory();

    public abstract Class<? extends Message> getClass(int msgCode);
}