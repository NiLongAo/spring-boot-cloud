package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootentity.vo.video.DeviceChannelTreeVo;
import cn.com.tzy.springbootstartervideobasic.common.CatalogEventConstant;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceChannelConvert;
import cn.com.tzy.springbootvideo.mapper.PlatformGbChannelMapper;
import cn.com.tzy.springbootvideo.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlatformGbChannelServiceImpl extends ServiceImpl<PlatformGbChannelMapper, PlatformGbChannel> implements PlatformGbChannelService{

    @Resource
    private ParentPlatformService parentPlatformService;
    @Resource
    private PlatformCatalogService platformCatalogService;
    @Resource
    private DeviceChannelService deviceChannelService;
    @Resource
    private DeviceService deviceService;

    @Override
    public RestResult<?> findDeviceChannelList(){
        List<Device> list = deviceService.list();
        List<DeviceChannel> list1 = deviceChannelService.list();
        List<DeviceChannelTreeVo> collect = list.stream().map(o -> DeviceChannelTreeVo.builder()
                .parentId(null)
                .type(1)
                .id(o.getDeviceId())
                .name(o.getName())
                .status(o.getOnline())
                .deviceId(o.getDeviceId())
                .build()
        ).collect(Collectors.toList());
        collect.addAll(list1.stream().map(o -> DeviceChannelTreeVo.builder()
                .parentId(o.getParentId())
                .type(2)
                .id(o.getChannelId())
                .name(o.getName())
                .status(o.getStatus())
                .deviceId(o.getDeviceId())
                .build()
        ).collect(Collectors.toList()));
        return RestResult.result(RespCode.CODE_0.getValue(),null,collect);
    }
    @Override
    public RestResult<?> findChannelBindKey(PlatformGbChannelParam param) {
        List<String> catalogIdList = new ArrayList<>();
        if(param.getIsSub() == ConstEnum.Flag.YES.getValue()){
            catalogIdList.addAll(platformCatalogService.findCatalogIdByAllSubList(param.getPlatformId(),param.getCatalogId()));
        }else {
            catalogIdList.add(param.getCatalogId());
        }
        List<DeviceChannel> allGbList = baseMapper.findDeviceChannelList(ConstEnum.Flag.YES.getValue(),null,param.getPlatformId(), null,null,null);
        List<DeviceChannel> useCatalogList = allGbList.stream().filter(o -> catalogIdList.contains(o.getCatalogId())).collect(Collectors.toList());
        List<String> allGbIdList = allGbList.stream().map(DeviceChannel::getChannelId).collect(Collectors.toList());
        if(!allGbIdList.isEmpty()){
            Map<String, List<DeviceChannel>> collect1 = allGbList.stream().collect(Collectors.groupingBy(DeviceChannel::getDeviceId));
            allGbIdList.addAll(collect1.keySet());
        }
        List<String> useCatalogGbIdList = useCatalogList.stream().map(DeviceChannel::getChannelId).collect(Collectors.toList());
        if(!useCatalogGbIdList.isEmpty()){
            Map<String, List<DeviceChannel>> collect2 = useCatalogList.stream().collect(Collectors.groupingBy(DeviceChannel::getDeviceId));
            useCatalogGbIdList.addAll(collect2.keySet());
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,new NotNullMap(){{
            put("allGbIdList",allGbIdList);
            put("useCatalogGbIdList",useCatalogGbIdList);
        }});
    }
    @Override
    public RestResult<?> insert(PlatformGbChannelSaveParam param) {
        ParentPlatform platform = parentPlatformService.getOne(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getServerGbId, param.getPlatformId()));
        if(platform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取上级平台信息");
        }
        List<DeviceChannel> deviceChannelList = null;
        if(param.getIsAll()== ConstEnum.Flag.YES.getValue()){
            deviceChannelList = baseMapper.findDeviceChannelList(ConstEnum.Flag.NO.getValue(),null,param.getPlatformId(), Collections.singletonList(param.getCatalogId()),null,null);
        }else{
            deviceChannelList = baseMapper.findDeviceChannelList(ConstEnum.Flag.NO.getValue(),null,param.getPlatformId(), Collections.singletonList(param.getCatalogId()),param.getGbIdList(),null);
        }
        if(deviceChannelList.isEmpty()){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取添加通道信息");
        }

        Map<String, DeviceChannel> collect = deviceChannelList.stream().collect(Collectors.toMap(DeviceChannel::getChannelId, o -> o));
        List<PlatformGbChannel> platformGbChannels = baseMapper.selectList(new LambdaQueryWrapper<PlatformGbChannel>().eq(PlatformGbChannel::getPlatformId, param.getPlatformId()).in(PlatformGbChannel::getDeviceChannelId, collect.keySet()));
        if(!platformGbChannels.isEmpty()){
            for (PlatformGbChannel platformGbChannel : platformGbChannels) {
                collect.remove(platformGbChannel.getDeviceChannelId());
            }
        }
        if(collect.isEmpty()){
            return RestResult.result(RespCode.CODE_0.getValue(),"添加成功");
        }
        List<PlatformGbChannel> platformGbChannelList = collect.values().stream().map(o -> PlatformGbChannel.builder().platformId(param.getPlatformId()).catalogId(param.getCatalogId()).deviceChannelId(o.getChannelId()).build()).collect(Collectors.toList());
        //批量保存
        saveBatch(platformGbChannelList);
        VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.ADD,platform.getServerGbId(),DeviceChannelConvert.INSTANCE.convertVoList(new ArrayList<>(collect.values())),null,null);
        return RestResult.result(RespCode.CODE_0.getValue(),"添加成功");
    }

    @Override
    public RestResult<?> delete(PlatformGbChannelSaveParam param) {
        ParentPlatform platform = parentPlatformService.getOne(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getServerGbId, param.getPlatformId()));
        if(platform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取上级平台信息");
        }
        List<String> catalogIdList = new ArrayList<>();
        if(param.getIsSub() == ConstEnum.Flag.YES.getValue()){
            catalogIdList.addAll(platformCatalogService.findCatalogIdByAllSubList(param.getPlatformId(),param.getCatalogId()));
        }else {
            catalogIdList.add(param.getCatalogId());
        }
        List<DeviceChannel> deviceChannelList;
        if(param.getIsAll()== ConstEnum.Flag.YES.getValue()){
            deviceChannelList = baseMapper.findDeviceChannelList(ConstEnum.Flag.YES.getValue(),null,param.getPlatformId(),catalogIdList,null,null);
        }else {
            deviceChannelList = baseMapper.findDeviceChannelList(ConstEnum.Flag.YES.getValue(),null,param.getPlatformId(),catalogIdList,param.getGbIdList(),null);
        }
        if(deviceChannelList.isEmpty()){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取移除通道信息");
        }
        List<String> collect = deviceChannelList.stream().map(DeviceChannel::getChannelId).collect(Collectors.toList());
        if(collect.isEmpty()){
            return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
        }
        baseMapper.delete(new LambdaQueryWrapper<PlatformGbChannel>().eq(PlatformGbChannel::getPlatformId,param.getPlatformId()).in(PlatformGbChannel::getCatalogId,catalogIdList).in(PlatformGbChannel::getDeviceChannelId,collect));
        VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.DEL,platform.getServerGbId(),DeviceChannelConvert.INSTANCE.convertVoList(new ArrayList<>(deviceChannelList)),null,null);
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @Override
    public void delPlatformGbChannel(String deviceId, String channelId) {
        baseMapper.delPlatformGbChannel(deviceId,channelId);
    }


}
