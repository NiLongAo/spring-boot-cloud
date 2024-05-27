package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.CompanyConference;
import cn.com.tzy.springbootfs.mapper.fs.CompanyConferenceMapper;
import cn.com.tzy.springbootfs.service.fs.CompanyConferenceService;
@Service
public class CompanyConferenceServiceImpl extends ServiceImpl<CompanyConferenceMapper, CompanyConference> implements CompanyConferenceService{

}
