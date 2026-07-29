package cn.com.tzy.springbootfs.config.fs;

import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动时将企业配置全量装载到 Redis。
 * 具体初始化逻辑已提取到 FsRuntimeConfigService，避免在此类中混入管理接口缓存刷新语义。
 */
@Log4j2
@Order(20)
@Component
public class CompanyRunner implements CommandLineRunner {

    @Resource
    private FsRuntimeConfigService runtimeConfigService;

    @Override
    public void run(String... args) throws Exception {
        runtimeConfigService.refreshAll();
    }
}
