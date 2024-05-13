package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootfs.mapper.fs.RouteGatewayMapper;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayService;
@Service
public class RouteGatewayServiceImpl extends ServiceImpl<RouteGatewayMapper, RouteGateway> implements RouteGatewayService{

}
