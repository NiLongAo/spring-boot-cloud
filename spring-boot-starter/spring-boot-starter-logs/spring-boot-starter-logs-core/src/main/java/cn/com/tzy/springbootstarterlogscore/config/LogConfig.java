package cn.com.tzy.springbootstarterlogscore.config;

import cn.com.tzy.springbootstarterlogscore.aspect.LogsAspect;
import cn.com.tzy.springbootstarterlogscore.core.LogApi;
import cn.com.tzy.springbootstarterlogscore.utils.IPUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
@Configuration
@Import({LogsAspect.class, LogApi.class})
public class LogConfig {

    /**
     * 根据ip获取ip地址信息注册
     * @return
     * @throws IOException
     */
    @Bean
    public Searcher searcher() throws IOException {
        InputStream inputStream = LogConfig.class.getResourceAsStream("/ip2region.xdb");//ip离线包
        byte[] bytes = inputStreamTobyte(inputStream);
        return Searcher.newWithBuffer(bytes);
    }

    @SneakyThrows
    public static byte[] inputStreamTobyte(InputStream inputStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int available = inputStream.available();
        byte[] buff = new byte[available]; //buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, available)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }
}
