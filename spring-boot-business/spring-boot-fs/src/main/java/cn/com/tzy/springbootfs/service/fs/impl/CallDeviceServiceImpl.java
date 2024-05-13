package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.CallDeviceMapper;
import cn.com.tzy.springbootentity.dome.fs.CallDevice;
import cn.com.tzy.springbootfs.service.fs.CallDeviceService;
@Service
public class CallDeviceServiceImpl extends ServiceImpl<CallDeviceMapper, CallDevice> implements CallDeviceService{

}
