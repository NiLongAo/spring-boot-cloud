package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootfs.mapper.fs.RouteGroupMapper;
import cn.com.tzy.springbootfs.service.fs.RouteGroupService;
@Service
public class RouteGroupServiceImpl extends ServiceImpl<RouteGroupMapper, RouteGroup> implements RouteGroupService{

}
