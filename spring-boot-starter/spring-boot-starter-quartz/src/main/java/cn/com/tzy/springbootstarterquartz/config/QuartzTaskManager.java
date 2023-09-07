package cn.com.tzy.springbootstarterquartz.config;

import cn.com.tzy.springbootcomm.excption.TimingException;
import cn.com.tzy.springbootstarterquartz.config.factory.TriggerManager;
import cn.com.tzy.springbootstarterquartz.config.mode.TimingModel;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Log4j2
public class QuartzTaskManager {
    private final Scheduler scheduler;
    private final TriggerManager triggerManager;
    private static QuartzTaskManager taskManager;
    private final Boolean initStatus;

    public QuartzTaskManager(Scheduler scheduler, TriggerManager triggerManager) throws SchedulerException {
        scheduler.clear();
        this.scheduler = scheduler;
        taskManager = this;
        boolean status = true;
        try {
            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("定时器调度器启动失败，定时器不可用！", e);
            status = false;
        }
        initStatus = status;
        this.triggerManager = triggerManager;
    }

    public static QuartzTaskManager getInstance(){
        return taskManager;
    }

    /**
     * 添加定时任务
     *
     * @param timingModel 任务model
     * @author YuanXiaohan
     * @date 2021/12/16 3:09 下午
     */
    public void addTask(TimingModel timingModel) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SchedulerException {
        checkTimingInit();
        // 构建任务信息
        JobDetail jobDetail = JobBuilder.newJob(timingModel.getTaskClass().getDeclaredConstructor().newInstance().getClass())
                .withDescription(timingModel.getDescription())
                .withIdentity(timingModel.getTaskName(), timingModel.getGroupName())
                .build();

        // 构建触发器
        Trigger trigger = triggerManager.build(timingModel);
        // 将任务参数放入触发器中
        if (timingModel.getParam() != null && !timingModel.getParam().isEmpty()) {
            trigger.getJobDataMap().putAll(timingModel.getParam());
        }
        // 启动任务
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 更新任务，任务的标示（由taskName和groupName组成）不变，任务的触发器（触发频率）发生变化
     *
     * @param timingModel 任务model
     * @author YuanXiaohan
     * @date 2021/12/16 3:15 下午
     */
    public void updateTask(TimingModel timingModel) throws SchedulerException {
        // 获取到任务
        TriggerKey triggerKey = TriggerKey.triggerKey(timingModel.getTaskName(), timingModel.getGroupName());

        // 构建触发器
        Trigger trigger = triggerManager.build(timingModel);
        // 将任务参数放入触发器中
        if (timingModel.getParam() != null && !timingModel.getParam().isEmpty()) {
            trigger.getJobDataMap().putAll(timingModel.getParam());
        }
        // 将新的触发器绑定到任务标示上重新执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 更新任务参数
     *
     * @param taskName  任务名
     * @param groupName 任务组名
     * @param param     参数
     */
    public void updateTask(String taskName, String groupName, Map<String, Object> param) throws SchedulerException {
        // 获取到任务
        TriggerKey triggerKey = TriggerKey.triggerKey(taskName, groupName);
        Trigger trigger = scheduler.getTrigger(triggerKey);

        //修改参数
        trigger.getJobDataMap().putAll(param);

        // 将新的触发器绑定到任务标示上重新执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 删除任务
     *
     * @param taskName  任务名
     * @param groupName 任务组
     */
    public void deleteTask(String taskName, String groupName) throws SchedulerException {
        // 暂停任务对应的触发器
        scheduler.pauseTrigger(TriggerKey.triggerKey(taskName, groupName));
        // 删除任务对应的触发器
        scheduler.unscheduleJob(TriggerKey.triggerKey(taskName, groupName));
        // 删除任务
        scheduler.deleteJob(JobKey.jobKey(taskName, groupName));
    }

    /**
     * 暂停任务
     *
     * @param taskName  添加任务时timingMode中的taskName
     * @param groupName 添加任务时timingMode中的groupName
     */
    public void pauseTask(String taskName, String groupName) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(taskName, groupName));
    }


    /**
     * 将暂停的任务恢复执行
     *
     * @param taskName  添加任务时timingMode中的taskName
     * @param groupName 添加任务时timingMode中的groupName
     * @author YuanXiaohan
     * @date 2021/12/16 3:13 下午
     */
    public void resumeTask(String taskName, String groupName) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(taskName, groupName));
    }

    /**
     * 启动所有任务
     *
     * @author YuanXiaohan
     * @date 2021/12/16 3:25 下午
     */
    public void startAllTasks() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭定时任务，回收所有的触发器资源
     *
     * @author YuanXiaohan
     * @date 2021/12/16 3:26 下午
     */
    public void shutdownAllTasks() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断定时任务是否启用
     * @param key
     * @return
     */
    public boolean checkExists(TriggerKey key) {
        try {
           return scheduler.checkExists(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 校验定时调度器是否初始化完成
     *
     * @author YuanXiaohan
     * @date 2021/12/16 2:28 下午
     */
    private void checkTimingInit() {
        if (!initStatus) {
            throw new TimingException("定时器未初始化，添加定时器失败!");
        }
    }


}
