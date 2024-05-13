package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.VdnPhone;
import cn.com.tzy.springbootfs.mapper.fs.VdnPhoneMapper;
import cn.com.tzy.springbootfs.service.fs.VdnPhoneService;
@Service
public class VdnPhoneServiceImpl extends ServiceImpl<VdnPhoneMapper, VdnPhone> implements VdnPhoneService{

}
