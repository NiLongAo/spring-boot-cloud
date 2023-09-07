package cn.com.tzy.springbootoa.service.impl;

import cn.com.tzy.springbootentity.dome.oa.Leave;
import cn.com.tzy.springbootoa.mapper.LeaveMapper;
import cn.com.tzy.springbootoa.service.LeaveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService{

}
