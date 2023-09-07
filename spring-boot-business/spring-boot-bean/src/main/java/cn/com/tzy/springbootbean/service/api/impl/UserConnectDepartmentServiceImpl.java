package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.UserConnectDepartmentMapper;
import cn.com.tzy.springbootbean.service.api.UserConnectDepartmentService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectDepartment;
import cn.com.tzy.springbootentity.param.bean.UserConnectDepartmentParam;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserConnectDepartmentServiceImpl extends ServiceImpl<UserConnectDepartmentMapper, UserConnectDepartment> implements UserConnectDepartmentService{


    @Override
    public RestResult<?> find(Long userId) {
        Set<Map> departmentIdList = baseMapper.findAllByUserId(userId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,departmentIdList);
    }

    @Override
    @CacheEvict(value = RedisCommon.USER_INFO,key = "#param.userId",allEntries = true)
    public RestResult<?> save(UserConnectDepartmentParam param) {
        List<UserConnectDepartment> userConnectDepartmentList = baseMapper.selectList(new QueryWrapper<UserConnectDepartment>().eq("user_id", param.userId));
        List<Long> userDepartmentList = userConnectDepartmentList.stream().map(UserConnectDepartment::getDepartmentId).collect(Collectors.toList());
        //要添加的值
        List<Long> addList = CollUtil.subtractToList(param.departmentList, userDepartmentList);
        //要删除的值
        List<Long> deleteList = CollUtil.subtractToList(userDepartmentList, param.departmentList);
        if(deleteList.size() > 0){
            baseMapper.deleteList(param.userId,deleteList);
        }
        if(addList.size() > 0){
            baseMapper.insertList(param.userId,addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
