package cn.com.tzy.springbootwebapi.log;

import cn.com.tzy.springbootentity.param.bean.LogsParam;
import cn.com.tzy.springbootfeignbean.api.sys.LogsServiceFeign;
import cn.com.tzy.springbootstarterlogscore.interfaces.LogsClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LogsClientImpl implements LogsClient {

    @Resource
    private LogsServiceFeign logsServiceFeign;

    @Override
    public void client(Integer type,String method, String url, String ip, String address, String param, String result, Integer duration) {
        LogsParam build = LogsParam.builder()
                .type(type)
                .ip(ip)
                .ipAttribution(address)
                .method(method)
                .api(url)
                .param(param)
                .result(result)
                .duration(duration)
                .build();
        logsServiceFeign.insert(build);
    }
}
