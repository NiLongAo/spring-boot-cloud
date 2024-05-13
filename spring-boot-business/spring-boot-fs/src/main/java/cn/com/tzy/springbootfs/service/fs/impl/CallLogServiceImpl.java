package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.CallLog;
import cn.com.tzy.springbootfs.mapper.fs.CallLogMapper;
import cn.com.tzy.springbootfs.service.fs.CallLogService;
@Service
public class CallLogServiceImpl extends ServiceImpl<CallLogMapper, CallLog> implements CallLogService{

}
