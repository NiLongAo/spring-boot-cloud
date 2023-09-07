package cn.com.tzy.springbootface.service;

import cn.com.tzy.springbootentity.dome.face.Person;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PersonService extends IService<Person> {

    /**
     * 根据图片编号获取人员信息
     * @param imgIdList
     * @return
     */
    List<Person> selectImgIdList(List<String> imgIdList);
}

