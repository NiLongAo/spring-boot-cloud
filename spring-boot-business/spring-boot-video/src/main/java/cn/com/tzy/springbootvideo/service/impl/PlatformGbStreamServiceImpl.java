package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.dome.video.PlatformGbStream;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootstartervideobasic.common.CatalogEventConstant;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootvideo.convert.video.GbStreamConvert;
import cn.com.tzy.springbootvideo.mapper.PlatformGbStreamMapper;
import cn.com.tzy.springbootvideo.service.GbStreamService;
import cn.com.tzy.springbootvideo.service.ParentPlatformService;
import cn.com.tzy.springbootvideo.service.PlatformCatalogService;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
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
public class PlatformGbStreamServiceImpl extends ServiceImpl<PlatformGbStreamMapper, PlatformGbStream> implements PlatformGbStreamService{

    @Resource
    private ParentPlatformService parentPlatformService;
    @Resource
    private GbStreamService gbStreamService;
    @Resource
    private PlatformCatalogService platformCatalogService;

    @Override
    public  RestResult<?> findGbStreamList() throws Exception {
        List<GbStream> gbStreamsList = gbStreamService.list();
        return RestResult.result(RespCode.CODE_0.getValue(),null,gbStreamsList);
    }

    @Override
    public  RestResult<?> findStreamBindKey(PlatformGbStreamParam param){
        List<String> catalogIdList = new ArrayList<>();
        if(param.getIsSub() == ConstEnum.Flag.YES.getValue()){
            catalogIdList.addAll(platformCatalogService.findCatalogIdByAllSubList(param.getPlatformId(),param.getCatalogId()));
        }else {
            catalogIdList.add(param.getCatalogId());
        }
        List<GbStream> gbStreamsList = baseMapper.findGbStreamsList(ConstEnum.Flag.YES.getValue(),param.getPlatformId(), null, null,null);
        List<String> allGbIdList = gbStreamsList.stream().map(GbStream::getGbId).collect(Collectors.toList());
        List<String> useCatalogGbIdList = gbStreamsList.stream().filter(o->catalogIdList.contains(o.getCatalogId())).map(GbStream::getGbId).collect(Collectors.toList());
        return RestResult.result(RespCode.CODE_0.getValue(),null,new NotNullMap(){{
            put("allGbIdList",allGbIdList);
            put("useCatalogGbIdList",useCatalogGbIdList);
        }});
    }

    @Override
    public RestResult<?> insert(PlatformGbStreamSaveParam param) {
        ParentPlatform platform = parentPlatformService.getOne(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getServerGbId, param.getPlatformId()));
        if(platform == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取上级平台信息");
        }
        Map<String, GbStream> gbStreamMap = null;
        if(param.getIsAll()== ConstEnum.Flag.YES.getValue()){
            List<GbStream> gbStreamsList = baseMapper.findGbStreamsList(ConstEnum.Flag.NO.getValue(), param.getPlatformId(), Collections.singletonList(param.getCatalogId()),null, null);
            gbStreamMap = gbStreamsList.stream().collect(Collectors.toMap(GbStream::getGbId, o -> o));
        }else {
            List<GbStream> gbStreamsList = baseMapper.findGbStreamsList(ConstEnum.Flag.NO.getValue(), param.getPlatformId(), Collections.singletonList(param.getCatalogId()),param.getGbIdList(), null);
            gbStreamMap = gbStreamsList.stream().collect(Collectors.toMap(GbStream::getGbId, o -> o));
        }
        if(gbStreamMap.isEmpty()){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取添加国标流信息");
        }
        List<PlatformGbStream> platformGbStreamList = baseMapper.selectList(new LambdaQueryWrapper<PlatformGbStream>().eq(PlatformGbStream::getPlatformId, param.getPlatformId()).in(PlatformGbStream::getGbStreamId, gbStreamMap.keySet()));
        if(!platformGbStreamList.isEmpty()){
            for (PlatformGbStream platformGbStream : platformGbStreamList) {
                gbStreamMap.remove(platformGbStream.getGbStreamId());
            }
        }
        if(gbStreamMap.isEmpty()){
            return RestResult.result(RespCode.CODE_0.getValue(),"添加成功");
        }
        List<PlatformGbStream> platformGbStreams = gbStreamMap.keySet().stream().map(o -> PlatformGbStream.builder().gbStreamId(o).platformId(param.getPlatformId()).catalogId(param.getCatalogId()).build()).collect(Collectors.toList());
        saveBatch(platformGbStreams);
        VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.ADD,param.getPlatformId(),null, GbStreamConvert.INSTANCE.convertListVo(new ArrayList<>(gbStreamMap.values())),null);
        return RestResult.result(RespCode.CODE_0.getValue(),"添加成功");
    }

    @Override
    public RestResult<?> delete(PlatformGbStreamSaveParam param) {
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
        Map<String, GbStream> gbStreamMap = null;
        if(param.getIsAll()== ConstEnum.Flag.YES.getValue()){
            List<GbStream> gbStreamsList = baseMapper.findGbStreamsList(ConstEnum.Flag.YES.getValue(), param.getPlatformId(), catalogIdList, null,null);
            gbStreamMap = gbStreamsList.stream().collect(Collectors.toMap(GbStream::getGbId, o -> o));
        }else {
            List<GbStream> gbStreamsList = gbStreamService.list(new LambdaQueryWrapper<GbStream>().in(GbStream::getGbId,param.getGbIdList()));
            gbStreamMap = gbStreamsList.stream().collect(Collectors.toMap(GbStream::getGbId, o -> o));
        }
        if(gbStreamMap.isEmpty()){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取移除国标流信息");
        }
        baseMapper.delete(new LambdaQueryWrapper<PlatformGbStream>().eq(PlatformGbStream::getPlatformId,param.getPlatformId()).in(PlatformGbStream::getCatalogId,catalogIdList).in(PlatformGbStream::getGbStreamId,gbStreamMap.keySet()));
        VideoService.getPlatformCatalogService().handleCatalogEvent(CatalogEventConstant.DEL,param.getPlatformId(),null, GbStreamConvert.INSTANCE.convertListVo(new ArrayList<>(gbStreamMap.values())),null);
        return RestResult.result(RespCode.CODE_0.getValue(),"移除成功");
    }

    @Override
    public void delPlatformGbStream(String app, String stream) {
        baseMapper.delPlatformGbStream(app,stream);
    }
}
