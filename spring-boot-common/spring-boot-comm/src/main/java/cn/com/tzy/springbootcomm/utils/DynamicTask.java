package cn.com.tzy.springbootcomm.utils;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 动态定时器任务
 */
@Log4j2
@Order(-1)
public class DynamicTask {

    private final ScheduledThreadPoolExecutor executor = ThreadUtil.createScheduledExecutor(200);//初始化200线程数
    private final Map<String, ScheduledFuture<?>> futureMap= new ConcurrentHashMap<>();
    private final Map<String, Runnable> runnableMap = new ConcurrentHashMap<>();

    public DynamicTask(){
        this.startCron("dynamic_task_execute",5*60, this::execute);
    }
    private void execute(){
        if (futureMap.size() > 0) {
            for (String key : futureMap.keySet()) {
                ScheduledFuture<?> future = futureMap.get(key);
                if (future.isDone() || future.isCancelled()) {
                    futureMap.remove(key);
                    runnableMap.remove(key);
                }
            }
        }
    }

    public ScheduledThreadPoolExecutor getExecutor(){
        return executor;
    }

    public void startCron(String key,int cycleForCatalog, Runnable task){
        startCron(key,cycleForCatalog,cycleForCatalog,task);
    }


    /**
     * 循环执行的任务
     * @param key 任务ID
     * @param task 任务
     * @param cycleForCatalog 间隔 秒
     */
    public void startCron(String key,int initialDelay,int cycleForCatalog, Runnable task) {
        if(ObjectUtils.isEmpty(key)) {
            return;
        }
        stop(key);
        ScheduledFuture<?> future = futureMap.get(key);
        if (future != null) {
            if (future.isCancelled()) {
                log.warn("任务【{}】已存在但是关闭状态！！！", key);
            } else {
                log.warn("任务【{}】已存在且已启动！！！", key);
                return;
            }
        }
        // scheduleWithFixedDelay 必须等待上一个任务结束才开始计时period， cycleForCatalog表示执行的间隔
        future = executor.scheduleAtFixedRate(()->{
            try {
                task.run();
            }catch (Exception exception){
                log.error(exception);
            }finally {
                log.info("任务【{}】 线程容量【运行线程：{}/空闲线程：{}/基本大小：{}】", key,executor.getActiveCount(),executor.getPoolSize(),executor.getCorePoolSize());
            }
        },initialDelay,cycleForCatalog,TimeUnit.SECONDS);
        futureMap.put(key, future);
        runnableMap.put(key, task);

    }

    /**
     * 延时任务
     * @param key 任务ID
     * @param task 任务
     * @param delay 延时 /秒
     */
    public void startDelay(String key, int delay, Runnable task) {
        if(ObjectUtils.isEmpty(key)) {
            return;
        }
        stop(key);
        ScheduledFuture future = futureMap.get(key);
        if (future != null) {
            if (future.isCancelled()) {
                log.warn("任务【{}】已存在但是关闭状态！！！", key);
            } else {
                log.warn("任务【{}】已存在且已启动！！！", key);
                return;
            }
        }
        future = executor.schedule(()->{
            try {
                task.run();
            }catch (Exception exception){
                log.error(exception);
            }finally {
                log.info("任务【{}】 线程容量【运行线程：{}/空闲线程：{}/基本大小：{}】 ", key,executor.getActiveCount(),executor.getPoolSize(),executor.getCorePoolSize());
            }
        },delay, TimeUnit.SECONDS);
        futureMap.put(key, future);
        runnableMap.put(key, task);
    }

    /**
     * 强行暫停
     * @param key
     * @return
     */
    public boolean stop(String key) {
        if(ObjectUtils.isEmpty(key)) {
            return false;
        }
        boolean result = false;
        if (isAlive(key)) {
            result = futureMap.get(key).cancel(false);
            futureMap.remove(key);
            runnableMap.remove(key);
        }
        return result;
    }

    public boolean contains(String key) {
        if(ObjectUtils.isEmpty(key)) {
            return false;
        }
        return futureMap.get(key) != null;
    }

    public Set<String> getAllKeys() {
        return futureMap.keySet();
    }

    public Runnable get(String key) {
        if(ObjectUtils.isEmpty(key)) {
            return null;
        }
        return runnableMap.get(key);
    }

    public boolean isAlive(String key) {
        return futureMap.get(key) != null && !futureMap.get(key).isDone() && !futureMap.get(key).isCancelled();
    }

}
