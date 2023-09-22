package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import cn.com.tzy.springbootstartervideobasic.common.CatalogEventConstant;
import cn.com.tzy.springbootstartervideobasic.enums.GbIdConstant;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.PlatformCatalogVo;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootvideo.convert.video.PlatformCatalogConvert;
import cn.com.tzy.springbootvideo.mapper.PlatformCatalogMapper;
import cn.com.tzy.springbootvideo.service.PlatformCatalogService;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlatformCatalogServiceImpl extends ServiceImpl<PlatformCatalogMapper, PlatformCatalog> implements PlatformCatalogService{

    @Resource
    private PlatformGbChannelService platformGbChannelService;
    @Resource
    private PlatformGbStreamService platformGbStreamService;



    @Override
    public PlatformCatalog findId(String id) {
        return baseMapper.findId(id);
    }

    @Override
    public List<DeviceChannel> queryCatalogInPlatform(String serverGbId) {
        return baseMapper.queryCatalogInPlatform(serverGbId);
    }

    @Override
    @Cacheable(value = RedisCommon.PLATFORM_CATALOG_PARENT,key = "#platformId")
    public RestResult<?> tree(String platformId) throws Exception {
        ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(platformId);
        List<PlatformCatalog> platformCatalogs = baseMapper.selectList(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getPlatformId,platformId));
        platformCatalogs.add(PlatformCatalog.builder()
                .parentId(null)
                .id(platform.getServerGbId())
                .platformId(platform.getServerGbId())
                .name(platform.getName())
                .civilCode(null)
                .businessGroupId(null)
                .build());
        List<TreeNode<PlatformCatalog>> treeNode =TreeUtil.getTree(platformCatalogs, PlatformCatalog::getParentId, PlatformCatalog::getId, null);
        List<Map> mapList = AppUtils.transformationTree("children", treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(),null,mapList);
    }
    @Override
    @Cacheable(value = RedisCommon.PLATFORM_CATALOG_PARENT_ID,key = " #platformId +'_'+ #catalogId")
    public List<String> findCatalogIdByAllSubList(String platformId,String catalogId)  {
        PlatformCatalog catalog = null;
        if(platformId.equals(catalogId)){
            catalog =PlatformCatalog.builder()
                    .type(0)
                    .id(catalogId)
                    .platformId(platformId)
                    .parentId(null)
                    .name("设备")
                    .build();
        }else {
            catalog = baseMapper.selectById(catalogId);
        }
        if(catalog == null){
            throw new RuntimeException("当前目录不存在");
        }
        List<PlatformCatalog> children = findChildren(false,catalog);
        if(children.isEmpty()){
            throw new RuntimeException("当前目录不存在");
        }
        List<String> collect = children.stream().filter(o->o.getType() == 0).map(PlatformCatalog::getId).collect(Collectors.toList());
        if(collect.isEmpty()){
            throw new RuntimeException("当前目录不存在");
        }
        return collect;
    }


    @Override
    @CacheEvict(value = {RedisCommon.PLATFORM_CATALOG_PARENT,RedisCommon.PLATFORM_CATALOG_PARENT_ID},allEntries = true)
    public RestResult<?> insert(PlatformCatalog param) {
        if(StringUtils.isEmpty(param.getId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取目录编号");
        }
        PlatformCatalog catalog = baseMapper.selectById(param.getId());
        if(catalog != null){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前目录编号已存在");
        }
        ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(param.getPlatformId());
        if(platform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取上级平台信息");
        }
        if(param.getId().length() >=6){
            param.setCivilCode(param.getId().substring(0,6));
        }
        if (platform.getTreeType().equals(GbIdConstant.Type.TYPE_215.getValue())) {
            if (platform.getServerGbId().equals(param.getParentId())) {
                // 第一层节点
                param.setBusinessGroupId(param.getId());
                param.setParentId(platform.getServerGbId());
            }else {
                // 获取顶层的
                PlatformCatalog topCatalog = baseMapper.selectOne(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getParentId, platform.getServerGbId()));
                param.setBusinessGroupId(topCatalog.getId());
            }
        }
        if (platform.getTreeType().equals(GbIdConstant.Type.TYPE_216.getValue())) {
            param.setCivilCode(param.getId());
            if (param.getPlatformId().equals(param.getParentId())) {
                // 第一层节点
                param.setParentId(platform.getServerGbId());
            }
        }
        //保存
        int insert = baseMapper.insert(param);
        if(insert>0){
            //上级发送通知
            PlatformCatalogVo convert = PlatformCatalogConvert.INSTANCE.convert(param);
            VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.ADD,platform.getServerGbId(), null,null, Collections.singletonList(convert));
            return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }

    @Override
    @CacheEvict(value = {RedisCommon.PLATFORM_CATALOG_PARENT,RedisCommon.PLATFORM_CATALOG_PARENT_ID},allEntries = true)
    public RestResult<?> update(PlatformCatalog param) {
        PlatformCatalog catalog = baseMapper.selectById(param.getId());
        if(catalog == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前目录不存在");
        }
        ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(catalog.getPlatformId());
        if(platform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取上级平台信息");
        }
        int update = baseMapper.updateById(param);
        if(update>0){
            //上级发送通知
            PlatformCatalogVo convert = PlatformCatalogConvert.INSTANCE.convert(param);
            VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.UPDATE,platform.getServerGbId(), null,null, Collections.singletonList(convert));
            return RestResult.result(RespCode.CODE_0.getValue(),"编辑成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"编辑失败");
        }
    }

    @Override
    @CacheEvict(value = {RedisCommon.PLATFORM_CATALOG_PARENT,RedisCommon.PLATFORM_CATALOG_PARENT_ID},allEntries = true)
    public RestResult<?> delete(String id) {
        List<PlatformCatalog> list;
        PlatformCatalog catalog = baseMapper.selectById(id);
        if(catalog == null){
            ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(id);
            if(platform == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"当前目录不存在");
            }
            list = baseMapper.selectList(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getPlatformId, platform.getServerGbId()));
        }else {
            list = findChildren(true,catalog);
        }
        for (PlatformCatalog platformCatalog : list) {
            deleteRelation(platformCatalog.getId(),0);
        }
        List<String> collect = list.stream().filter(o->o.getType() == 0).map(PlatformCatalog::getId).collect(Collectors.toList());
        int i = baseMapper.deleteBatchIds(collect);
        if(i > 0){
            List<PlatformCatalogVo> platformCatalogVos = PlatformCatalogConvert.INSTANCE.convertVo(list);
            VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.DEL,null, null,null, platformCatalogVos);
            return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"删除失败");
        }
    }

    /**
     * 删除关联
     * @param id 目录编号
     * @param type 删除类型 0.全部 1.通道 2.流
     * @return
     */
    @Override
    @CacheEvict(value = {RedisCommon.PLATFORM_CATALOG_PARENT,RedisCommon.PLATFORM_CATALOG_PARENT_ID},allEntries = true)
    public RestResult<?> deleteRelation(String id, Integer type) {
        PlatformCatalog catalog = baseMapper.selectById(id);
        if(catalog == null){
            ParentPlatformVo platform = VideoService.getParentPlatformService().getParentPlatformByServerGbId(id);
            if(platform == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"当前目录不存在");
            }
            catalog = PlatformCatalog.builder()
                    .id(platform.getServerGbId())
                    .platformId(platform.getServerGbId())
                    .parentId(platform.getServerGbId())
                    .build();
        }
        List<PlatformCatalog> platformCatalogs = null;
        if(type == 1){
            platformCatalogs =baseMapper.findGbStream(catalog.getId(),catalog.getPlatformId());
            platformGbChannelService.delete(PlatformGbChannelSaveParam.builder()
                    .platformId(catalog.getPlatformId())
                    .catalogId(catalog.getId())
                    .isAll(ConstEnum.Flag.YES.getValue())
                    .isSub(ConstEnum.Flag.NO.getValue())
                    .build());
        }else if(type == 2){
            platformCatalogs =baseMapper.findGbChannel(catalog.getId(),catalog.getPlatformId());
            platformGbStreamService.delete(PlatformGbStreamSaveParam.builder()
                    .platformId(catalog.getPlatformId())
                    .catalogId(catalog.getId())
                    .isAll(ConstEnum.Flag.YES.getValue())
                    .isSub(ConstEnum.Flag.NO.getValue())
                    .build());
        }else {
            platformCatalogs=baseMapper.findGbStream(catalog.getId(),catalog.getPlatformId());
            platformCatalogs.addAll(baseMapper.findGbChannel(catalog.getId(),catalog.getPlatformId()));
            platformGbChannelService.delete(PlatformGbChannelSaveParam.builder()
                    .platformId(catalog.getPlatformId())
                    .catalogId(catalog.getId())
                    .isAll(ConstEnum.Flag.YES.getValue())
                    .isSub(ConstEnum.Flag.NO.getValue())
                    .build());
            platformGbStreamService.delete(PlatformGbStreamSaveParam.builder()
                    .platformId(catalog.getPlatformId())
                    .catalogId(catalog.getId())
                    .isAll(ConstEnum.Flag.YES.getValue())
                    .isSub(ConstEnum.Flag.NO.getValue())
                    .build());
        }
        if(!platformCatalogs.isEmpty()){
            List<PlatformCatalogVo> platformCatalogVos = PlatformCatalogConvert.INSTANCE.convertVo(platformCatalogs);
            VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.DEL,null, null,null, platformCatalogVos);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    private List<PlatformCatalog> findChildren(boolean isGb,PlatformCatalog catalog){
        List<PlatformCatalog> platformCatalogList = new ArrayList<>();
        //放入自己
        platformCatalogList.add(catalog);
        if(isGb){
            //查询国标流相关信息
            List<PlatformCatalog> gbStreamList =baseMapper.findGbStream(catalog.getId(),catalog.getPlatformId());
            if(!gbStreamList.isEmpty()){
                platformCatalogList.addAll(gbStreamList);
            }
            List<PlatformCatalog> gbChannelList =baseMapper.findGbChannel(catalog.getId(),catalog.getPlatformId());
            if(!gbChannelList.isEmpty()){
                platformCatalogList.addAll(gbChannelList);
            }
        }
        List<PlatformCatalog> platformCatalogs = baseMapper.selectList(new LambdaQueryWrapper<PlatformCatalog>().eq(PlatformCatalog::getParentId, catalog.getId()));
        for (PlatformCatalog platformCatalog : platformCatalogs) {
            List<PlatformCatalog> children = findChildren(isGb,platformCatalog);
            if(!children.isEmpty()){
                platformCatalogList.addAll(children);
            }
        }
        return platformCatalogList;
    }
}
