//package cn.com.tzy.springbootsms.config.init;
//
//import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
//import cn.com.tzy.springbootcomm.spring.SpringContextHolder;
//import cn.com.tzy.springbootcomm.utils.AppUtils;
//import cn.com.tzy.springbootentity.dome.sms.Quartz;
//import cn.com.tzy.springbootsms.mapper.QuartzMapper;
//import cn.com.tzy.springbootstarterquartz.config.QuartzTaskManager;
//import cn.com.tzy.springbootstarterquartz.config.mode.CronTimingModel;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import lombok.extern.log4j.Log4j2;
//import org.codehaus.jackson.type.TypeReference;
//import org.quartz.SchedulerException;
//import org.quartz.TriggerKey;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.List;
//
///**
// * https://gitee.com/youngkid/springboot-quartz/blob/master/src/main/java/org/demo/QuartzApplication.java
// */
//@Log4j2
//@Component
//public class QuartzApplicationRunner implements ApplicationRunner {
//
//    @Autowired
//    private QuartzMapper quartzMapper;
//    @Autowired
//    private AppConfig appConfig;
//
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //加载定时器
//        init();
//        appConfig.setQuartzApplicationRunner(this);
//    }
//
//    /**
//     * 加载所有运行中的定时器
//     * @throws Exception
//     */
//    public void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, SchedulerException, IllegalAccessException {
//        QuartzTaskManager quartzTaskManager = SpringContextHolder.getBean(QuartzTaskManager.class);
//
//        List<Quartz> list = quartzMapper.selectList(new QueryWrapper<Quartz>().eq("task_status", ConstEnum.Flag.YES.getValue()));
//        if(list.isEmpty()){
//            return;
//        }
//        List<CronTimingModel> cronTimingModelList  = AppUtils.convertValue2(list,new TypeReference<List<CronTimingModel>>(){});
//        for (CronTimingModel cronTimingModel : cronTimingModelList) {
//            // 获取到任务
//            TriggerKey triggerKey = TriggerKey.triggerKey(cronTimingModel.getTaskName(), cronTimingModel.getGroupName());
//            if(!quartzTaskManager.checkExists(triggerKey)){
//                quartzTaskManager.addTask(cronTimingModel);
//            }else {
//                quartzTaskManager.updateTask(cronTimingModel);
//            }
//        }
//    }
//
//    public void destroy() throws NacosException {
//        //服务注册成功后手动添加到nacos中
//        log.info("关闭定时器");
//        QuartzTaskManager quartzTaskManager = SpringContextHolder.getBean(QuartzTaskManager.class);
//        quartzTaskManager.shutdownAllTasks();
//    }
//}
