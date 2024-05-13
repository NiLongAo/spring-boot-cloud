package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.PushLog;
import cn.com.tzy.springbootfs.mapper.fs.PushLogMapper;
import cn.com.tzy.springbootfs.service.fs.PushLogService;
@Service
public class PushLogServiceImpl extends ServiceImpl<PushLogMapper, PushLog> implements PushLogService{

}
