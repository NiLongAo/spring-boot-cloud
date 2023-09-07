package cn.com.tzy.springbootstarterlogscore.interfaces;


public interface LogsClient {

    public void client(Integer type,String method , String url , String ip,String address, String param, String result, Integer duration);
}
