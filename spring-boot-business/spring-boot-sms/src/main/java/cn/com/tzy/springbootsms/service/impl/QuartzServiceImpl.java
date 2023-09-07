package cn.com.tzy.springbootsms.service.impl;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootsms.service.QuartzService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.sms.Quartz;
import cn.com.tzy.springbootsms.mapper.QuartzMapper;

@Service
public class QuartzServiceImpl extends ServiceImpl<QuartzMapper, Quartz> implements QuartzService {

    @Override
    public PageResult page() {
        return null;
    }

    @Override
    public RestResult<?> addJob(String jobClassName, String jobGroupName, String cronExpression) throws Exception {
        return null;
    }

    @Override
    public RestResult<?> updateJob(String jobClassName, String jobGroupName, String cronExpression) throws Exception {
        return null;
    }

    @Override
    public RestResult<?> deleteJob(String jobClassName, String jobGroupName) throws Exception {
        return null;
    }

    @Override
    public RestResult<?> pauseJob(String jobClassName, String jobGroupName) throws Exception {
        return null;
    }

    @Override
    public RestResult<?> resumejob(String jobClassName, String jobGroupName) throws Exception {
        return null;
    }
}
