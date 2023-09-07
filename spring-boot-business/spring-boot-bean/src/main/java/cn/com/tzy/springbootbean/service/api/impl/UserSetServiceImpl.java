package cn.com.tzy.springbootbean.service.api.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootbean.mapper.sql.UserSetMapper;
import cn.com.tzy.springbootentity.dome.bean.UserSet;
import cn.com.tzy.springbootbean.service.api.UserSetService;
@Service
public class UserSetServiceImpl extends ServiceImpl<UserSetMapper, UserSet> implements UserSetService{

}
