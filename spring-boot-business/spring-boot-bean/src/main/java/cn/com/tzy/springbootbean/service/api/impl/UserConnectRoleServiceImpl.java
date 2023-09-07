package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.UserConnectRoleMapper;
import cn.com.tzy.springbootbean.service.api.UserConnectRoleService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectRole;
import cn.com.tzy.springbootentity.param.bean.UserConnectRoleParam;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserConnectRoleServiceImpl extends ServiceImpl<UserConnectRoleMapper, UserConnectRole> implements UserConnectRoleService{



    @Override
    public RestResult<?> find(Long userId) {
        Set<Map> roleIdList = baseMapper.findAllByUserId(userId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,roleIdList);
    }

    @Override
    @CacheEvict(value = RedisCommon.USER_INFO,key = "#param.userId",allEntries = true)
    public RestResult<?> save(UserConnectRoleParam param) {
        List<UserConnectRole> userConnectRoleList = baseMapper.selectList(new QueryWrapper<UserConnectRole>().eq("user_id", param.userId));
        List<Long> userRoleList = userConnectRoleList.stream().map(UserConnectRole::getRoleId).collect(Collectors.toList());
        //要删除的值
        List<Long> deleteList =userConnectRoleList.stream().filter(num -> !param.roleList.contains(num.getRoleId())).map(UserConnectRole::getRoleId).collect(Collectors.toList());
        //要添加的值
        List<Long> addList = param.roleList.stream().filter(num -> !userRoleList.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteList(param.userId,deleteList);
        }
        if(addList.size() > 0){
            baseMapper.insertList(param.userId,addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
