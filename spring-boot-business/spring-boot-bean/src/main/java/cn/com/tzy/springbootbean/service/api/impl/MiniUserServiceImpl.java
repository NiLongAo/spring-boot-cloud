package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.service.api.MiniUserService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootbean.mapper.sql.MiniUserMapper;
import cn.com.tzy.springbootentity.dome.bean.MiniUser;
@Service
public class MiniUserServiceImpl extends ServiceImpl<MiniUserMapper, MiniUser> implements MiniUserService {

}
