package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.RoleMapper;
import cn.com.tzy.springbootbean.service.api.RoleService;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService{

    @Override
    public PageResult page(RoleParam param) {
        int total = baseMapper.findPageCount(param);
        List<Role> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putString("roleName", obj.getRoleName());
            map.putString("memo", obj.getMemo());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(Long id, String memo, String roleName) {
        Role role = null;
        if(id != null){
            role = baseMapper.selectOne(new QueryWrapper<Role>().eq("id", id));
            if(role == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到角色信息");
            }
        }else {
            role = new Role();
        }
        role.setId(id);
        role.setRoleName(roleName);
        role.setMemo(memo);
        boolean b = super.saveOrUpdate(role);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }

    @Override
    public RestResult<?> roleSelect(List<Long> roleIdList, String roleName, Integer limit) {
        List<Role> roleList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(roleIdList)){
            roleList.addAll(baseMapper.selectBatchIds(roleIdList));
        }
        roleList.addAll(baseMapper.selectNameLimit(roleIdList,roleName, limit));
        List<NotNullMap> data = new ArrayList<>();
        roleList.forEach(obj ->{
            NotNullMap map = new NotNullMap();
            map.putLong("id",obj.getId());
            map.putString("name", obj.getRoleName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);

    }
}
