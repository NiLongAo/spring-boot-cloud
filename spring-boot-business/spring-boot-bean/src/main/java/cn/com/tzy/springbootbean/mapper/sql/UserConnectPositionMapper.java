package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.UserConnectPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserConnectPositionMapper extends BaseMapper<UserConnectPosition> {
    Set<Map> findAllByUserId(@Param("userId") Long userId);


    int insertList(@Param("userId") Long userId, @Param("addList") List<Long> addList);

    void deleteList(@Param("userId") Long userId, @Param("deleteList") List<Long> deleteList);
}
