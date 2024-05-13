package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.VdnSchedule;
import cn.com.tzy.springbootfs.mapper.fs.VdnScheduleMapper;
import cn.com.tzy.springbootfs.service.fs.VdnScheduleService;
@Service
public class VdnScheduleServiceImpl extends ServiceImpl<VdnScheduleMapper, VdnSchedule> implements VdnScheduleService{

}
