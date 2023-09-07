package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.Quartz;
import com.baomidou.mybatisplus.extension.service.IService;
public interface QuartzService extends IService<Quartz>{

    PageResult page();

    RestResult<?> addJob(String jobClassName, String jobGroupName, String cronExpression) throws Exception;

    RestResult<?> updateJob(String jobClassName, String jobGroupName, String cronExpression) throws Exception;

    RestResult<?> deleteJob(String jobClassName, String jobGroupName) throws Exception;

    RestResult<?> pauseJob(String jobClassName, String jobGroupName) throws Exception;

    RestResult<?> resumejob(String jobClassName, String jobGroupName) throws Exception;

}
