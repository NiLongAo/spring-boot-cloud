package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.RouteGatewayGroupMapper;
import cn.com.tzy.springbootentity.dome.fs.RouteGatewayGroup;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayGroupService;
@Service
public class RouteGatewayGroupServiceImpl extends ServiceImpl<RouteGatewayGroupMapper, RouteGatewayGroup> implements RouteGatewayGroupService{

}
