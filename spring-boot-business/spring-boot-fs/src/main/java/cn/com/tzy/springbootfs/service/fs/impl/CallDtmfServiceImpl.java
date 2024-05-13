package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.CallDtmf;
import cn.com.tzy.springbootfs.mapper.fs.CallDtmfMapper;
import cn.com.tzy.springbootfs.service.fs.CallDtmfService;
@Service
public class CallDtmfServiceImpl extends ServiceImpl<CallDtmfMapper, CallDtmf> implements CallDtmfService{

}
