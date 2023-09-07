package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformGbStream;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootvideo.convert.video.ParentPlatformConvert;
import cn.com.tzy.springbootvideo.mapper.ParentPlatformMapper;
import cn.com.tzy.springbootvideo.service.ParentPlatformService;
import cn.com.tzy.springbootvideo.service.PlatformCatalogService;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class ParentPlatformServiceImpl extends ServiceImpl<ParentPlatformMapper, ParentPlatform> implements ParentPlatformService{

    @Resource
    private PlatformCatalogService platformCatalogService;
    @Resource
    private PlatformGbChannelService platformGbChannelService;
    @Resource
    private PlatformGbStreamService platformGbStreamService;

    @Override
    public List<Map> findChannelSource(String serverGbId, String channelId) {
        return baseMapper.findChannelSource(serverGbId,channelId);
    }

    @Override
    public List<ParentPlatform> queryPlatFormListForGBWithGBId(String channelId, List<String> allPlatformId) {
        return baseMapper.queryPlatFormListForGBWithGBId(channelId,allPlatformId);
    }

    @Override
    public List<ParentPlatform> queryPlatFormListForStreamWithGBId(String app, String stream, List<String> allPlatformId) {
        return baseMapper.queryPlatFormListForStreamWithGBId(app,stream,allPlatformId);
    }
    
    @Override
    public RestResult<?> findSipList() {
        List<Address> sipList = RedisService.getRegisterServerManager().getSipList();
        return RestResult.result(RespCode.CODE_0.getValue(),null,sipList);
    }

    @Override
    public PageResult findPage(ParentPlatformPageParam param) {
        Page<ParentPlatform> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<ParentPlatform> wrapper = new LambdaQueryWrapper<ParentPlatform>()
                .and(StringUtils.isNotEmpty(param.query), o -> o.like(ParentPlatform::getServerIp, param.query).or().like(ParentPlatform::getServerGbId, param.query))
                .eq(param.status != null,ParentPlatform::getStatus,param.status);
        return MyBatisUtils.selectPage(baseMapper, page, wrapper);
    }

    @Override
    public RestResult<?> insert(ParentPlatform param) {
        if(param.getCatalogGroup() == null || param.getCatalogGroup() <= 0){
            param.setCatalogGroup(1);
        }
        if (StringUtils.isEmpty(param.getAdministrativeDivision())) {
            // 行政区划默认去编号的前6位
            param.setAdministrativeDivision(param.getServerGbId().substring(0,6));
        }
        if (StringUtils.isEmpty(param.getServerGbDomain())) {
            // SIP服务国标域默认去编号的前10位
            param.setServerGbDomain(param.getServerGbId().substring(0,10));
        }
        if(StringUtils.isEmpty(param.getUsername())){
            param.setUsername(param.getServerGbId());
        }
        if(StringUtils.isEmpty(param.getCatalogId())){
            param.setCatalogId(param.getServerGbId());
        }
        param.setCatalogId(param.getServerGbId());
        baseMapper.insert(param);
        //是否启用发送注册
        if(param.getEnable()== ConstEnum.Flag.YES.getValue()){
            ParentPlatformVo convert = ParentPlatformConvert.INSTANCE.convert(param);
            //向上级注册
            RedisUtils.redisTemplate.convertAndSend(VideoConstant.VIDEO_SEND_SIP_REGISTER_MESSAGE, convert);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }

    @Override
    public RestResult<?> update(ParentPlatform param) {
        ParentPlatform parentPlatform = baseMapper.selectById(param.getId());
        if(parentPlatform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取平台信息");
        }
        if(!param.getServerGbId().equals(parentPlatform.getServerGbId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"平台国标编码不一致");
        }
        if(param.getCatalogGroup() == null || param.getCatalogGroup() <= 0){
            param.setCatalogGroup(1);
        }
        if (param.getAdministrativeDivision() == null) {
            // 行政区划默认去编号的前6位
            param.setAdministrativeDivision(param.getServerGbId().substring(0,6));
        }
        if(!param.getTreeType().equals(parentPlatform.getTreeType())){
            // 目录结构发生变化，清空之前的关联关系
            platformCatalogService.remove(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getPlatformId,param.getServerGbId()));
            platformGbChannelService.remove(new LambdaQueryWrapper<PlatformGbChannel>().eq(PlatformGbChannel::getPlatformId,param.getServerGbId()));
            platformGbStreamService.remove(new LambdaQueryWrapper<PlatformGbStream>().eq(PlatformGbStream::getPlatformId,param.getServerGbId()));
        }
        param.setCatalogId(param.getServerGbId());
        baseMapper.updateById(param);
        //先注销原平台
        if(parentPlatform.getStatus() == ConstEnum.Flag.YES.getValue()){
            ParentPlatformVo old = ParentPlatformConvert.INSTANCE.convert(parentPlatform);
            VideoService.getParentPlatformService().unregister(old,null,null);
        }
        //是否启用发送注册
        if(param.getEnable()== ConstEnum.Flag.YES.getValue()){
            ParentPlatformVo now = ParentPlatformConvert.INSTANCE.convert(param);
            //向上级注册
            RedisUtils.redisTemplate.convertAndSend(VideoConstant.VIDEO_SEND_SIP_REGISTER_MESSAGE, now);
        }

        return RestResult.result(RespCode.CODE_0.getValue(),"编辑成功");
    }

    @Override
    public RestResult<?> delete(Long id) {
        ParentPlatform parentPlatform = baseMapper.selectById(id);
        if(parentPlatform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取平台信息");
        }
        if(parentPlatform.getStatus() == ConstEnum.Flag.YES.getValue()){
            ParentPlatformVo vo = ParentPlatformConvert.INSTANCE.convert(parentPlatform);
            VideoService.getParentPlatformService().unregister(vo,null,null);
        }
        platformCatalogService.remove(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getPlatformId,parentPlatform.getServerGbId()));
        platformGbChannelService.remove(new LambdaQueryWrapper<PlatformGbChannel>().eq(PlatformGbChannel::getPlatformId,parentPlatform.getServerGbId()));
        platformGbStreamService.remove(new LambdaQueryWrapper<PlatformGbStream>().eq(PlatformGbStream::getPlatformId,parentPlatform.getServerGbId()));
        baseMapper.deleteById(id);
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @Override
    public RestResult<?> detail(Long id) {
        ParentPlatform parentPlatform = baseMapper.selectById(id);
        if(parentPlatform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取平台信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,parentPlatform);
    }

}
