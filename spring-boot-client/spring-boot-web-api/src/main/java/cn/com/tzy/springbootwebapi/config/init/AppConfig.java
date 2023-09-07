package cn.com.tzy.springbootwebapi.config.init;

import cn.com.tzy.springbootcomm.constant.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;

@Component
@Order(1)
@Getter
@Setter
public class AppConfig {


    public File appDir;
    public File tempDir;

    @Autowired
    private ServletContext sc;

    @SneakyThrows
    @PostConstruct
    public void init() {
        this.appDir = new File(sc.getRealPath(File.separator));
        this.tempDir = new File(appDir, Constant.PATH_TEMP);
    }
}
