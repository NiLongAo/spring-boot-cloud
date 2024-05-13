package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.CallDetail;
import cn.com.tzy.springbootfs.mapper.fs.CallDetailMapper;
import cn.com.tzy.springbootfs.service.fs.CallDetailService;
@Service
public class CallDetailServiceImpl extends ServiceImpl<CallDetailMapper, CallDetail> implements CallDetailService{

}
