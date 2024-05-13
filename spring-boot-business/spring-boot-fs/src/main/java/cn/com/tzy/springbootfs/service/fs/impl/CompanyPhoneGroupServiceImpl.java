package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.CompanyPhoneGroupMapper;
import cn.com.tzy.springbootentity.dome.fs.CompanyPhoneGroup;
import cn.com.tzy.springbootfs.service.fs.CompanyPhoneGroupService;
@Service
public class CompanyPhoneGroupServiceImpl extends ServiceImpl<CompanyPhoneGroupMapper, CompanyPhoneGroup> implements CompanyPhoneGroupService{

}
