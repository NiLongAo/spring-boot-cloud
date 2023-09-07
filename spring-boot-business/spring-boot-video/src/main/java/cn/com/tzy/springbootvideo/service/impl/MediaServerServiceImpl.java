package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.MediaServer;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootstartervideobasic.vo.media.ZLMServerConfig;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.media.client.ZlmService;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.InviteStreamManager;
import cn.com.tzy.springbootvideo.convert.video.MediaServerConvert;
import cn.com.tzy.springbootvideo.mapper.MediaServerMapper;
import cn.com.tzy.springbootvideo.service.MediaServerService;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MediaServerServiceImpl extends ServiceImpl<MediaServerMapper, MediaServer> implements MediaServerService{

    @Resource
    private ZlmService zlmService;

    @Override
    public PageResult findPage(MediaServerPageParam param) {
        Page<MediaServer> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<MediaServer> wrapper = new LambdaQueryWrapper<MediaServer>().and(StringUtils.isNotEmpty(param.query), o -> o.like(MediaServer::getIp, param.query)).eq(param.online != null,MediaServer::getStatus,param.online);
        return MyBatisUtils.selectPage(baseMapper, page, wrapper);
    }

    @Override
    public RestResult<?> save(MediaServerSaveParam param) {
        MediaServer convert = MediaServerConvert.INSTANCE.convert(param);
        MediaServerVo vo = MediaServerConvert.INSTANCE.convert(convert);
        if(StringUtils.isEmpty(param.getId())){
            Integer integer = baseMapper.selectCount(new LambdaQueryWrapper<MediaServer>().eq(MediaServer::getIp, convert.getIp()).eq(MediaServer::getHttpPort, convert.getHttpPort()));
            if(integer > 0){
                return RestResult.result(RespCode.CODE_2.getValue(),"当前流媒体IP端口已存在，请更换");
            }
            convert.setId(RandomUtil.randomString(19));
        }
        convert.setStatus(ConstEnum.Flag.NO.getValue());
        saveOrUpdate(convert);
        if(convert.getEnable() == ConstEnum.Flag.YES.getValue()){
            ZLMServerConfig zlmServerConfig = MediaClient.getZLMServerConfig(vo);
            if(zlmServerConfig == null){
                return  RestResult.result(RespCode.CODE_2.getValue(),"流媒体服务链接失败！");
            }
            if(!convert.getId().equals(zlmServerConfig.getGeneralMediaServerId())){
                removeById(convert.getId());
                convert.setId(zlmServerConfig.getGeneralMediaServerId());
                saveOrUpdate(convert);
            }
            zlmServerConfig.setIp(convert.getIp());
            zlmServerConfig.setRestart(ConstEnum.Flag.YES.getValue());
            zlmService.zlmOnline(zlmServerConfig);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }

    @Override
    public RestResult<?> remove(String id) {
        MediaServer mediaServer = baseMapper.selectById(id);
        if(mediaServer == null){
            return  RestResult.result(RespCode.CODE_2.getValue(),"未获取流媒体信息");
        }
        MediaServerVo vo = MediaServerConvert.INSTANCE.convert(mediaServer);
        zlmService.zlmOffline(vo);
        RedisService.getMediaServerManager().removeCount(vo.getId());
        baseMapper.deleteById(mediaServer.getId());
        return RestResult.result(RespCode.CODE_0.getValue(),"移除成功");
    }

    @Override
    public RestResult<?> findPlayUrl(String deviceId, String channelId) {
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfo(null, deviceId, channelId, null,null);
        if(inviteInfo.getStreamInfo() == null){
            return RestResult.result(RespCode.CODE_0.getValue(),"当前流未播放");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,inviteInfo.getStreamInfo());

    }

    @Override
    public RestResult<?> detail(String id) {
        MediaServer mediaServer = baseMapper.selectById(id);
        if(mediaServer == null){
            return  RestResult.result(RespCode.CODE_2.getValue(),"未获取流媒体信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,mediaServer);
    }
}
