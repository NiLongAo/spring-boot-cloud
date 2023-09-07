package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.MediaServer;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.MediaServerManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootvideo.convert.video.MediaServerConvert;
import cn.com.tzy.springbootvideo.service.MediaServerService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MediaServerVoServiceImpl implements MediaServerVoService {

    @Resource
    private VideoProperties videoProperties;
    @Resource
    private MediaServerService mediaServerService;

    @Override
    public MediaServerVo findOnLineMediaServerId(String mediaServerId) {
        if(StringUtils.isEmpty(mediaServerId)){
            log.info("[流媒体信息]：未获取到流媒体编号信息");
            return null;
        }
        MediaServer one = mediaServerService.getOne(new LambdaQueryWrapper<MediaServer>().eq(MediaServer::getEnable,ConstEnum.Flag.YES.getValue()).eq(MediaServer::getId, mediaServerId).eq(MediaServer::getStatus,ConstEnum.Flag.YES.getValue()));
        if(one == null){
            log.info("[流媒体信息]：未获取到流媒体信息");
            return null;
        }
        return MediaServerConvert.INSTANCE.convert(one);
    }


    @Override
    public MediaServerVo findMediaServerId(String mediaServerId) {
        if(StringUtils.isEmpty(mediaServerId)){
            log.info("[流媒体信息]：未获取到流媒体编号信息");
            return null;
        }
        MediaServer one = mediaServerService.getOne(new LambdaQueryWrapper<MediaServer>().eq(MediaServer::getId, mediaServerId));
        if(one == null){
            log.info("[流媒体信息]：未获取到流媒体信息");
            return null;
        }
        return MediaServerConvert.INSTANCE.convert(one);
    }
    @Override
    public List<MediaServerVo> findConnectZlmList() {
        List<MediaServer> list = mediaServerService.list(new LambdaQueryWrapper<MediaServer>().eq(MediaServer::getEnable,ConstEnum.Flag.YES.getValue()));
        List<MediaServer> collect = list.stream().filter(o -> o.getKeepaliveTime() == null || o.getKeepaliveTime().compareTo(DateUtil.offsetSecond(new Date(), -(o.getHookAliveInterval() + VideoConstant.DELAY_TIME))) < 0).collect(Collectors.toList());
        return MediaServerConvert.INSTANCE.convertVo(collect);
    }

    @Override
    public MediaServerVo findMediaServerForMinimumLoad(DeviceVo deviceVo){
        if(deviceVo == null){
            log.error("未获取设备信息");
            return null;
        }
        MediaServerVo mediaServerItem = null;
        if(StringUtils.isNotEmpty(deviceVo.getMediaServerId())){
            mediaServerItem = findOnLineMediaServerId(deviceVo.getMediaServerId());
            if (videoProperties.getUseClientOnLineZlm() && ObjectUtils.isEmpty(mediaServerItem)) {
                mediaServerItem = findMediaServerForMinimumLoad();
                VideoService.getDeviceService().updateZlm(deviceVo.getDeviceId(),mediaServerItem.getId());
            }
        }else {
            mediaServerItem = findMediaServerForMinimumLoad();
        }
        if (mediaServerItem == null) {
            log.warn("[获取可用的ZLM节点]未找到可使用的ZLM...");
        }
        return mediaServerItem;
    }

    @Override
    public MediaServerVo findMediaServerForMinimumLoad() {
        MediaServerManager mediaServerManager = RedisService.getMediaServerManager();
        String mediaServerId = mediaServerManager.getMediaServerForMinimumLoad();
        MediaServer mediaServer = null;
        if(StringUtils.isNotBlank(mediaServerId)){
            mediaServer = mediaServerService.getById(mediaServerId);
        }
        if(mediaServer == null){
            List<MediaServer> list = mediaServerService.list(new LambdaQueryWrapper<MediaServer>().eq(MediaServer::getEnable,ConstEnum.Flag.YES.getValue()).eq(MediaServer::getStatus, ConstEnum.Flag.YES.getValue()));
            if(list.isEmpty()){
                log.info("[流媒体信息] 未获取到在线的流媒体服务");
                return null;
            }
            mediaServer = list.get(0);
        }
        return MediaServerConvert.INSTANCE.convert(mediaServer);
    }

    @Override
    public int updateById(MediaServerVo mediaServerVo) {
        MediaServer convert = MediaServerConvert.INSTANCE.convert(mediaServerVo);
        boolean b = mediaServerService.updateById(convert);
        return b?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }
    @Override
    public int updateStatus(String mediaServerId, Integer status) {
        if(StringUtils.isBlank(mediaServerId) || status == null){
            log.info("[流媒体信息] 获取信息错误 mediaServerId：{}，status：{}",mediaServerId,status);
            return ConstEnum.Flag.NO.getValue();
        }
        MediaServer build = MediaServer.builder().id(mediaServerId).status(status).build();
        if(ConstEnum.Flag.YES.getValue() == status){
            build.setKeepaliveTime(new Date());
        }
        boolean b = mediaServerService.updateById(build);
        return b?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }
}
