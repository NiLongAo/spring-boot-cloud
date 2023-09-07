package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.MiniUserMapper;
import cn.com.tzy.springbootbean.service.api.MiniService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.MiniUser;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootbean.mapper.sql.MiniMapper;
@Service
public class MiniServiceImpl extends ServiceImpl<MiniMapper, Mini> implements MiniService {

    @Autowired
    private MiniUserMapper miniUserMapper;


    @Override
    public RestResult<?> findWebUserId(Long userId) {
        Mini mini =  baseMapper.findWebUserId(userId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,mini);
    }

    @Override
    public RestResult<?> saveMiniUser(MiniUserParam param) {
        Mini mini = baseMapper.selectOne(new LambdaQueryWrapper<Mini>().eq(Mini::getOpenId, param.openId));
        if(mini == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取微信用户信息");
        }
        Integer selectCount = miniUserMapper.selectCount(new LambdaQueryWrapper<MiniUser>().eq(MiniUser::getUserId, param.userId).or(o->o.eq(MiniUser::getMiniId, mini.getId())));
        if(selectCount > 0){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前用户已绑定微信用户，请先解绑");
        }
        MiniUser build = MiniUser.builder()
                .miniId(mini.getId())
                .userId(param.userId)
                .build();
        miniUserMapper.insert(build);
        return RestResult.result(RespCode.CODE_0);
    }

    @Override
    public RestResult<?> unbindMiniWeb(MiniUserParam param) {
        if(param.miniId == null && param.userId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取用户编号");
        }
        int delete = miniUserMapper.delete(new LambdaQueryWrapper<MiniUser>().and(o -> o.eq(MiniUser::getUserId, param.userId).or(r -> r.eq(MiniUser::getMiniId, param.miniId))));
        if(delete <= 0){
            return RestResult.result(RespCode.CODE_2.getValue(),"解绑失败，未获取用户信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"解绑成功");
    }
}
