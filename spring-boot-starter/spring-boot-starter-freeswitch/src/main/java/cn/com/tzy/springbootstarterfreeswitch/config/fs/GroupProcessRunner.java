package cn.com.tzy.springbootstarterfreeswitch.config.fs;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 进入到队列的电话，需要定时找空闲坐席
 */
@Log4j2
@Component
public class GroupProcessRunner implements CommandLineRunner {

    @Resource
    private DynamicTask dynamicTask;
    @Override
    public void run(String... args) throws Exception {
        //进入到队列的电话，需要定时找空闲坐席
        this.dynamicTask.startCron("GROUP_HANDLER_TASK",5,2, ()->{
            FsService.getGroupMemoryInfoService().execute();
        });
    }
}
