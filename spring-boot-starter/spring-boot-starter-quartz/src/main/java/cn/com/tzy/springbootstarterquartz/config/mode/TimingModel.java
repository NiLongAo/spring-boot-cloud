package cn.com.tzy.springbootstarterquartz.config.mode;

import cn.com.tzy.springbootcomm.excption.TimingException;
import cn.com.tzy.springbootstarterquartz.config.dome.Quartz;
import cn.com.tzy.springbootstarterquartz.config.task.QuartzTaskJob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author Xiaohan.Yuan
 * @version 1.0.0
 * @ClassName TimingModel.java
 * @Description 构建定时的model
 * @createTime 2021年12月16日
 */
@Getter
@Setter
@NoArgsConstructor
public class TimingModel extends Quartz {

    /**
     * 该定时的任务处理器
     */
    private Class<? extends QuartzTaskJob> taskClass;

    /**
     * 任务参数,可在具体的QuartzTaskJob实现中获取这些参数
     * */
    private Map<String, Object> param;


    public TimingModel(Class<? extends QuartzTaskJob> taskClass, String taskName, String groupName, String description, Integer type, Map<String, Object> param) {
        this.classesName =taskClass.getPackage().getName();
        this.taskClass = taskClass;
        this.taskName = taskName;
        this.groupName = groupName;
        this.description = description;
        this.type = type;
        this.param = param;
    }

    public TimingModel(Class<? extends QuartzTaskJob> taskClass, String taskName, String groupName, String description, Integer type) {
        this.classesName =taskClass.getPackage().getName();
        this.taskClass = taskClass;
        super.taskName = taskName;
        this.groupName = groupName;
        this.description = description;
        this.type = type;
    }

    public Class<? extends QuartzTaskJob> getTaskClass(){
        if(!ObjectUtils.isEmpty(this.taskClass)){
            return taskClass;
        }
        Class<? extends QuartzTaskJob> task = null;
        try {
            task = (Class<? extends QuartzTaskJob>) Class.forName(this.classesName);
            this.taskClass = task;
        } catch (ClassNotFoundException e) {
           throw new TimingException("未获取当前类，或当前类未继承 QuartzTaskJob 类");
        }
        return this.taskClass;
    }

}
