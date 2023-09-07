package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.UserConnectPositionMapper;
import cn.com.tzy.springbootbean.service.api.UserConnectPositionService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.UserConnectPosition;
import cn.com.tzy.springbootentity.param.bean.UserConnectPositionParam;
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
public class UserConnectPositionServiceImpl extends ServiceImpl<UserConnectPositionMapper, UserConnectPosition> implements UserConnectPositionService{


    @Override
    public RestResult<?> find(Long userId) {
        Set<Map> positionIdList = baseMapper.findAllByUserId(userId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,positionIdList);
    }

    @CacheEvict(value = RedisCommon.USER_INFO,key = "#param.userId",allEntries = true)
    @Override
    public RestResult<?> save(UserConnectPositionParam save) {
        List<UserConnectPosition> userConnectPositions = baseMapper.selectList(new QueryWrapper<UserConnectPosition>().eq("user_id", save.userId));
        List<Long> userPositionsList = userConnectPositions.stream().map(UserConnectPosition::getPositionId).collect(Collectors.toList());
        //要删除的值
        List<Long> deleteList =userConnectPositions.stream().filter(num -> !save.positionList.contains(num.getPositionId())).map(UserConnectPosition::getPositionId).collect(Collectors.toList());
        //要添加的值
        List<Long> addList = save.positionList.stream().filter(num -> !userPositionsList.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteList(save.userId,deleteList);
        }
        if(addList.size() > 0){
            baseMapper.insertList(save.userId,addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
