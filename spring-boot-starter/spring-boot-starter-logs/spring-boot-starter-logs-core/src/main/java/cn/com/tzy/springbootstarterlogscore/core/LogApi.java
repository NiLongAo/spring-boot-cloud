package cn.com.tzy.springbootstarterlogscore.core;


import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterlogscore.interfaces.LogsClient;
import cn.com.tzy.springbootstarterlogscore.utils.IPUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
public class LogApi {

    public void logs(Integer type,String method , String url , String ip, NotNullMap paramMap,Object resultObj,long duration){
        //获取地址
        LogsClient logsClient = SpringUtil.getBean(LogsClient.class);
        String ipAdder = IPUtil.getIpAdder(ip);
        logsClient.client(type,method,url,ip,ipAdder, JSONUtil.toJsonPrettyStr(paramMap),JSONUtil.toJsonPrettyStr(resultObj),(int)duration);
    }

}
