package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.PrivilegeMapper;
import cn.com.tzy.springbootbean.service.api.PrivilegeService;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Privilege;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, Privilege> implements PrivilegeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(String id, String menuId, String privilegeName, String requestUrl, String memo) {

        Privilege privilege = null;
        if (id != null) {
            privilege = baseMapper.selectOne(new QueryWrapper<Privilege>().eq("id", id));
            if (privilege == null) {
                return RestResult.result(RespCode.CODE_2.getValue(), "未获取到权限信息");
            }
        } else {
            privilege = new Privilege();
        }
        privilege.setId(id);
        privilege.setPrivilegeName(privilegeName);
        privilege.setMemo(memo);
        privilege.setMenuId(menuId);
        privilege.setRequestUrl(requestUrl);
        boolean b = super.saveOrUpdate(privilege);
        if (b) {
            return RestResult.result(RespCode.CODE_0.getValue(), "保存成功");
        } else {
            return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
        }


    }

    /**
     * 初始化权限信息（保存到Redis）
     *
     * @return
     */
    @Override
    public boolean init() {
        boolean flag = false;
        try {
            List<Privilege> allUrlAndPrivilege = baseMapper.findEnabledAll(ConstEnum.Flag.YES.getValue());
            Map<String,Object> allUrlPrivilege = new HashMap<>();
            allUrlAndPrivilege.forEach(obj -> {
                String[] split = obj.getRequestUrl().split(",");//多个页面url组合时
                for (String url : split) {
                    Object object = allUrlPrivilege.get(url);
                    if(object == null){
                        object = new HashSet<String>();
                        allUrlPrivilege.put(url,object);
                    }
                    Set<String> set = (Set<String>) object;
                    set.add(obj.getId());
                }
            });
            if (RedisUtils.hasKey(Constant.ALL_URL_KEY)) {
                RedisUtils.del(Constant.ALL_URL_KEY);
            }
            RedisUtils.hmset(Constant.ALL_URL_KEY, allUrlPrivilege);
            flag = true;
        } catch (Exception e) {
            log.error("初始化权限信息错误 :", e);
        }
        return flag;
    }
}

