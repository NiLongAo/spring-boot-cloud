package cn.com.tzy.springbootface.mapper;

import cn.com.tzy.springbootentity.dome.face.Person;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {
    /**
     * 根据图片编号集合获取人员信息
     *
     * @param imgIdList
     * @return
     */
    List<Person> selectImgIdList(@Param("imgIdList") List<String> imgIdList);
}