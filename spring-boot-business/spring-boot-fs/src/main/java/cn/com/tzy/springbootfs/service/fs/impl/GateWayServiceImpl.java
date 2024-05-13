package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.GateWayMapper;
import cn.com.tzy.springbootentity.dome.fs.GateWay;
import cn.com.tzy.springbootfs.service.fs.GateWayService;
@Service
public class GateWayServiceImpl extends ServiceImpl<GateWayMapper, GateWay> implements GateWayService{

}
