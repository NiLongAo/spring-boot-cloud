package cn.com.tzy.springbootservicexxljob.core.alarm;

import cn.com.tzy.springbootservicexxljob.core.model.XxlJobInfo;
import cn.com.tzy.springbootservicexxljob.core.model.XxlJobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog);

}
