package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.PositionConnectMenuMapper;
import cn.com.tzy.springbootbean.mapper.sql.PositionMapper;
import cn.com.tzy.springbootbean.service.api.PositionConnectMenuService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Position;
import cn.com.tzy.springbootentity.dome.bean.PositionConnectMenu;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionConnectMenuServiceImpl extends ServiceImpl<PositionConnectMenuMapper, PositionConnectMenu> implements PositionConnectMenuService{

    @Autowired
    PositionMapper positionMapper;

    @Override
    public RestResult<?> findPositionPrivilegeList(Long positionId) {
        List<String> privilegeList = baseMapper.findPositionPrivilegeList(positionId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,privilegeList);
    }

    @Override
    public RestResult<?> save(Long positionId, List<String> privilegeList) {
        if(positionId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取职位编号");
        }
        Position position = positionMapper.selectById(positionId);
        if(position == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取职位信息");
        }
        List<String> privileges = baseMapper.findPositionPrivilegeList(positionId);
        //要删除的值
        List<String> deleteList =privileges.stream().filter(num -> !privilegeList.contains(num)).collect(Collectors.toList());
        //要添加的值
        List<String> addList = privilegeList.stream().filter(num -> !privileges.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deletePositionConnectMenu(position.getId(),deleteList);
        }
        if(addList.size() > 0){
            baseMapper.savePositionConnectMenu(position.getId(),addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
