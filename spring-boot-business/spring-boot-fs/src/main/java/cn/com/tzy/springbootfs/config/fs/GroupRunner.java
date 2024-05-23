package cn.com.tzy.springbootfs.config.fs;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Log4j2
@Order(20)
@Component
public class GroupRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        initGroup();
    }


    private void initGroup(){

    }
}
