package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.VdnCodeMapper;
import cn.com.tzy.springbootentity.dome.fs.VdnCode;
import cn.com.tzy.springbootfs.service.fs.VdnCodeService;
@Service
public class VdnCodeServiceImpl extends ServiceImpl<VdnCodeMapper, VdnCode> implements VdnCodeService{

}
