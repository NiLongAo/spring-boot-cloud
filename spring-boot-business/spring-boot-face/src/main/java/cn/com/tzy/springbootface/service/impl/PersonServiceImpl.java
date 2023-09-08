package cn.com.tzy.springbootface.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.face.Person;
import cn.com.tzy.springbootface.mapper.PersonMapper;
import cn.com.tzy.springbootface.service.PersonService;

import java.util.List;

/**
 * @author TZY
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {

    @Override
    public List<Person> selectImgIdList(List<String> imgIdList) {
        return baseMapper.selectImgIdList(imgIdList);
    }
}

