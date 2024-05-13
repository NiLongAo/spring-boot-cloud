package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.OverflowFrontMapper;
import cn.com.tzy.springbootentity.dome.fs.OverflowFront;
import cn.com.tzy.springbootfs.service.fs.OverflowFrontService;
@Service
public class OverflowFrontServiceImpl extends ServiceImpl<OverflowFrontMapper, OverflowFront> implements OverflowFrontService{

}
