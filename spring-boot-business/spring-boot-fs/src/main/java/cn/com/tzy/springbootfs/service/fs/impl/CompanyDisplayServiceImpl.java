package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.CompanyDisplay;
import cn.com.tzy.springbootfs.mapper.fs.CompanyDisplayMapper;
import cn.com.tzy.springbootfs.service.fs.CompanyDisplayService;
@Service
public class CompanyDisplayServiceImpl extends ServiceImpl<CompanyDisplayMapper, CompanyDisplay> implements CompanyDisplayService{

}
