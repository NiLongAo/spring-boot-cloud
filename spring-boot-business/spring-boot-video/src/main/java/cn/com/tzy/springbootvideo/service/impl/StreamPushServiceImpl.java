package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootentity.dome.video.StreamPush;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import cn.com.tzy.springbootentity.vo.video.StreamPushVo;
import cn.com.tzy.springbootstartervideobasic.enums.StreamType;
import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.StreamInfo;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootstartervideocore.service.video.PlatformCatalogVoService;
import cn.com.tzy.springbootvideo.mapper.StreamPushMapper;
import cn.com.tzy.springbootvideo.service.GbStreamService;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import cn.com.tzy.springbootvideo.service.StreamPushService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StreamPushServiceImpl extends ServiceImpl<StreamPushMapper, StreamPush> implements StreamPushService{

    @Resource
    private VideoProperties videoProperties;
    @Resource
    private GbStreamService gbStreamService;
    @Resource
    private PlatformGbStreamService platformGbStreamService;
    @Resource
    private MediaServerVoService mediaServerVoService;

    @Override
    public PageResult findPage(StreamPushPageParam param) {
        int total = baseMapper.findPageCount(param);
        List<StreamPushVo> pageResult = baseMapper.findPageResult(param);
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, pageResult);
    }

    @Override
    public List<StreamPush> findMediaServiceNotGbId(String mediaServiceId) {
        return baseMapper.findMediaServiceNotGbId(mediaServiceId);
    }

    @Override
    public RestResult<?> save(StreamPushSaveParam param) {
        StreamPush build = StreamPush.builder()
                .app(param.getApp())
                .stream(param.getStream())
                .status(ConstEnum.Flag.NO.getValue())
                .pushIng(ConstEnum.Flag.NO.getValue())
                .onSelf(ConstEnum.Flag.YES.getValue())
                .build();
        StreamPush streamPush = baseMapper.selectOne(new LambdaQueryWrapper<StreamPush>().eq(StreamPush::getApp, param.getApp()).eq(StreamPush::getStream, param.getStream()));
        if(streamPush != null){
            build.setId(streamPush.getId());
        }
        GbStream one = null;
        if(streamPush != null){
            one = gbStreamService.getOne(new LambdaQueryWrapper<GbStream>().eq(GbStream::getApp, param.getApp()).eq(GbStream::getStream, param.getStream()));
            if(one != null && !one.getGbId().equals(param.getGbId())){
                return RestResult.result(RespCode.CODE_2.getValue(),"国标编号不可修改");
            }
        }
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
        saveOrUpdate(build);
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        PlatformCatalogVoService platformCatalogVoService = VideoService.getPlatformCatalogService();
        StreamPush streamPush = baseMapper.selectById(id);
        if(streamPush == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取推流");
        }
        //等待修改
        GbStream one = gbStreamService.getOne(new LambdaQueryWrapper<GbStream>().eq(GbStream::getApp, streamPush.getApp()).eq(GbStream::getStream, streamPush.getStream()));
        if(one != null){
            //1.移除推流绑定推送上级信息
            platformGbStreamService.delPlatformGbStream(streamPush.getApp(),streamPush.getStream());
            //2.移除国标流信息
            gbStreamService.removeById(one.getGbStreamId());
        }
        if(StringUtils.isNotEmpty(streamPush.getMediaServerId())){
            MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(streamPush.getMediaServerId());
            if(mediaServerVo != null){
                MediaRestResult result = MediaClient.getMediaList(mediaServerVo, "__defaultVhost__", null, streamPush.getApp(), streamPush.getStream());
                if(result != null && result.getCode() ==RespCode.CODE_0.getValue()){
                    if( ObjectUtils.isEmpty(result.getData())){
                        //3.移除推流信息
                        baseMapper.deleteById(id);
                    }else {
                        return RestResult.result(RespCode.CODE_2.getValue(),"当前推流正在接收,请先关闭推流");
                    }
                }else{
                    return RestResult.result(RespCode.CODE_2.getValue(),"流媒体获取推流信息失败");
                }
            }else {
                //3.移除推流信息
                baseMapper.deleteById(id);
            }
        }else {
            //3.移除推流信息
            baseMapper.deleteById(id);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"关闭成功");
    }

    @Override
    public RestResult<?> getPlayUrl(Long id) {
        StreamPush streamPush = baseMapper.selectById(id);
        if(streamPush == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取推流");
        }else if(streamPush.getStatus() != ConstEnum.Flag.YES.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前推流未上线");
        }else if (StringUtils.isEmpty(streamPush.getMediaServerId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取当前流媒体信息");
        }
        MediaServerVoService mediaServer = VideoService.getMediaServerService();
        MediaServerVo mediaServerVo = mediaServer.findOnLineMediaServerId(streamPush.getMediaServerId());
        StreamInfo streamInfo = MediaClient.getStreamInfo(mediaServerVo, streamPush.getApp(), streamPush.getStream(), null, null);
        return RestResult.result(RespCode.CODE_0.getValue(),null,streamInfo);
    }

    @Override
    public RestResult<?> detail(Long id) {
        StreamPushVo vo = baseMapper.detail(id);
        if(vo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取推流信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,vo);
    }

}
