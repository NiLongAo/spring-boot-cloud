package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.CompanyPhoneMapper;
import cn.com.tzy.springbootentity.dome.fs.CompanyPhone;
import cn.com.tzy.springbootfs.service.fs.CompanyPhoneService;
@Service
public class CompanyPhoneServiceImpl extends ServiceImpl<CompanyPhoneMapper, CompanyPhone> implements CompanyPhoneService{

}
