package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import cn.com.tzy.springbootentity.vo.video.StreamProxyVo;
import cn.com.tzy.springbootstartervideobasic.enums.ProxyTypeEnum;
import cn.com.tzy.springbootstartervideobasic.enums.StreamType;
import cn.com.tzy.springbootstartervideobasic.vo.media.ZLMServerConfig;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.StreamInfo;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootvideo.convert.video.StreamProxyConvert;
import cn.com.tzy.springbootvideo.mapper.StreamProxyMapper;
import cn.com.tzy.springbootvideo.service.GbStreamService;
import cn.com.tzy.springbootvideo.service.StreamProxyService;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class StreamProxyServiceImpl extends ServiceImpl<StreamProxyMapper, StreamProxy> implements StreamProxyService{

    @Resource
    private GbStreamService gbStreamService;
    @Resource
    private VideoProperties videoProperties;


    @Override
    public PageResult findPage(StreamProxyPageParam param) {
        int total = baseMapper.findPageCount(param);
        List<StreamProxyVo> pageResult = baseMapper.findPageResult(param);
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, pageResult);
    }

    @Override
    public RestResult<?> save(StreamProxySaveParam param) {
        String name = ProxyTypeEnum.getName(param.getType());
        if(StringUtils.isEmpty(name)){
            return RestResult.result(RespCode.CODE_2.getValue(),"拉流类型错误");
        }
        MediaServerVoService mediaServerVoService = VideoService.getMediaServerService();
        MediaServerVo mediaServerVo = null;
        if(StringUtils.isNotEmpty(param.getMediaServerId())){
             mediaServerVo = mediaServerVoService.findOnLineMediaServerId(param.getMediaServerId());
        }
        if(mediaServerVo == null) {
             mediaServerVo = mediaServerVoService.findMediaServerForMinimumLoad();
        }
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取在线的流媒体服务");
        }
        if(StringUtils.isEmpty(param.getFfmpegCmdKey())){
            RestResult<?> result = findFfmpegCmd(mediaServerVo.getId());
            if(result.getCode() == RespCode.CODE_0.getValue()){
                param.setFfmpegCmdKey(MapUtil.getStr((NotNullMap) result.getData(), "ffmpegCmd",null));
            }
        }
        int port;
        String schemaForUri;
        String schema = param.getSchemaFromFFmpegCmd();
        if ("rtsp".equalsIgnoreCase(schema)) {
            port = mediaServerVo.getRtspPort();
            schemaForUri = schema;
        }else if ("flv".equalsIgnoreCase(schema)) {
            port = mediaServerVo.getRtmpPort();
            schemaForUri = schema;
        }else {
            port = mediaServerVo.getRtmpPort();
            schemaForUri = schema;
        }

        StreamProxy build = StreamProxy.builder()
                .type(param.getType())
                .app(param.getApp())
                .stream(param.getStream())
                .name(param.getName())
                .status(ConstEnum.Flag.NO.getValue())
                .mediaServerId(mediaServerVo.getId())
                .url(param.getUrl())
                .srcUrl(param.getSrcUrl())
                .dstUrl(String.format("%s://%s:%s/%s/%s",schemaForUri, "127.0.0.1", port, param.getApp(), param.getStream()))
                .timeoutMs(param.getTimeoutMs())
                .ffmpegCmdKey(param.getFfmpegCmdKey())
                .rtpType(param.getRtpType())
                .enable(param.getEnable())
                .enableAudio(param.getEnableAudio())
                .enableMp4(param.getEnableMp4())
                .enableRemoveNoneReader(param.getEnableRemoveNoneReader())
                .enableDisableNoneReader(param.getEnableDisableNoneReader())
                .build();
        StreamProxy streamProxy = baseMapper.selectOne(new LambdaQueryWrapper<StreamProxy>().eq(StreamProxy::getApp, param.getApp()).eq(StreamProxy::getStream, param.getStream()));
        if(streamProxy != null){
            build.setId(streamProxy.getId());
        }
        saveOrUpdate(build);
        GbStream one = gbStreamService.getOne(new LambdaQueryWrapper<GbStream>().eq(GbStream::getApp, param.getApp()).eq(GbStream::getStream, param.getStream()));
        if(StringUtils.isNotEmpty(param.getGbId())){
            GbStream build1 = GbStream.builder()
                    .app(param.getApp())
                    .stream(param.getStream())
                    .gbId(param.getGbId())
                    .name(param.getName())
                    .longitude(param.getLongitude())
                    .latitude(param.getLatitude())
                    .streamType(StreamType.PULL.getValue())
                    .build();
            if(one != null){
                build1.setGbStreamId(one.getGbStreamId());
            }
            gbStreamService.saveOrUpdate(build1);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        StreamProxy streamProxy = baseMapper.selectById(id);
        if(streamProxy == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取拉流代理");
        }
        VideoService.getStreamProxyService().delete(streamProxy.getApp(),streamProxy.getStream());
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @Override
    public RestResult<?> findFfmpegCmd(String mediaServerId) {
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findOnLineMediaServerId(mediaServerId);
        ZLMServerConfig zlmServerConfig = MediaClient.getZLMServerConfig(mediaServerVo);
        if(zlmServerConfig == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到当前流媒体信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,new NotNullMap(){{
            putString("ffmpegCmd",zlmServerConfig.getFfmpegCmd());
        }});
    }

    @Override
    public RestResult<?> start(Long id) {
        StreamProxy streamProxy = baseMapper.selectById(id);
        if(streamProxy == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取拉流代理");
        }
        boolean start = VideoService.getStreamProxyService().start(StreamProxyConvert.INSTANCE.convert(streamProxy));
        if(start){
            return RestResult.result(RespCode.CODE_0.getValue(),"启用成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"启用失败");
        }
    }

    @Override
    public RestResult<?> stop(Long id) {
        StreamProxy streamProxy = baseMapper.selectById(id);
        if(streamProxy == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取拉流代理");
        }
        boolean stop = VideoService.getStreamProxyService().stop(streamProxy.getApp(), streamProxy.getStream());
        if(stop){
            return RestResult.result(RespCode.CODE_0.getValue(),"停用成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"停用失败");
        }
    }

    @Override
    public RestResult<?> detail(Long id) {
        StreamProxyVo vo = baseMapper.detail(id);
        if(vo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取拉流代理");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,vo);
    }

    @Override
    public RestResult<?> getPlayUrl(Long id) {
        StreamProxy streamProxy = baseMapper.selectById(id);
        if(streamProxy == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取推流");
        }else if(streamProxy.getStatus() != ConstEnum.Flag.YES.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前推流未上线");
        }else if (StringUtils.isEmpty(streamProxy.getMediaServerId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取当前流媒体信息");
        }
        MediaServerVoService mediaServer = VideoService.getMediaServerService();
        MediaServerVo mediaServerVo = mediaServer.findOnLineMediaServerId(streamProxy.getMediaServerId());
        StreamInfo streamInfo = MediaClient.getStreamInfo(mediaServerVo, streamProxy.getApp(), streamProxy.getStream(), null, null);
        return RestResult.result(RespCode.CODE_0.getValue(),null,streamInfo);
    }
}
