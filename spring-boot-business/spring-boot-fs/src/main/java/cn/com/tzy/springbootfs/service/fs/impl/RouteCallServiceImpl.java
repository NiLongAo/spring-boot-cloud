package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.RouteCallMapper;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
@Service
public class RouteCallServiceImpl extends ServiceImpl<RouteCallMapper, RouteCall> implements RouteCallService{

}
