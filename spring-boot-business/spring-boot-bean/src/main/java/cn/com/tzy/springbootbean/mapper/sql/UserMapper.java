package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    SecurityBaseUser findLoginAccount(@Param("loginAccount") String loginAccount);

    SecurityBaseUser findLoginUserId(@Param("id") Long id);

    SecurityBaseUser selectPhone(@Param("phone") String phone);

    SecurityBaseUser selectOpenId(@Param("openId") String openId);

    int findPageCount(UserParam userPageModel);

    List<User> findPageResult(UserParam userPageModel);


    List<User> findRoleIdList(@Param("idList") List<Long> idList);

    List<User> findDepartmentIdList(@Param("idList") List<Long> idList);

    List<User> findPositionIdList(@Param("idList") List<Long> idList);

    int findChoiceUserPageCount(UserParam userPageModel);

    List<User> findChoiceUserPageResult(UserParam userPageModel);

    List<User> selectNameLimit(@Param("userIdList") List<Long> userIdList, @Param("userName") String userName, @Param("limit") Integer limit);

}