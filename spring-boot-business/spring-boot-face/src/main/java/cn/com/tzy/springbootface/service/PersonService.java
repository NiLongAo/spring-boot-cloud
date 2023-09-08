package cn.com.tzy.springbootface.service;

import cn.com.tzy.springbootentity.dome.face.Person;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author TZY
 */
public interface PersonService extends IService<Person> {

    /**
     * 根据图片编号获取人员信息
     * @param imgIdList 图片编号集合
     * @return 人员信息集合
     */
    List<Person> selectImgIdList(List<String> imgIdList);
}

