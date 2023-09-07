package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.PlayService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * 国标录像相关信息接口
 */
@Log4j2
@RestController("ApiGbVideoController")
@RequestMapping(value = "/api/gb/video")
public class GbVideoController  extends ApiController {

    @Resource
    private SIPCommander sipCommander;
    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private SipServer sipServer;
    @Resource
    private PlayService playService;

    /**
     * 录像查询列表
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @GetMapping("/list")
    public DeferredResult<RestResult> list(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime){
        log.info(String.format("录像信息查询 API调用，deviceId：%s ，startTime：%s， endTime：%s",deviceGbId, startTime, endTime));
        VideoRestResult<RestResult> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        String uuid = RandomUtil.randomString(32);
        int sn = RandomUtil.randomInt(100000, 999999);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_RECORDINFO,deviceGbId,sn);
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceGbId);
        try {
            sipCommander.recordInfoQuery(sipServer,deviceVo,channelId,startTime,endTime,sn,null,null,null,error->{
                log.error(String.format("查询录像失败, status: %s,message: %s",error.getStatusCode(),error.getMsg()));
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(error.getStatusCode(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 查询录像: {}", e.getMessage());
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"[命令发送失败] 查询录像"));
        }
        return result;
    }

    /**
     * 开始下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param downloadSpeed 下载倍速
     * @return
     */
    @GetMapping("/download/start")
    public DeferredResult<RestResult> start(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime,@RequestParam("downloadSpeed")Integer downloadSpeed){
        log.info(String.format("历史媒体下载 API调用，deviceId：%s，channelId：%s，downloadSpeed：%s", deviceGbId, channelId, downloadSpeed));
        String uuid = RandomUtil.randomString(32);
        String key = DeferredResultHolder.CALLBACK_CMD_DOWNLOAD + deviceGbId + channelId;
        VideoRestResult<RestResult> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceGbId);
        if (deviceVo == null) {
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",deviceGbId)));
            return result;
        }
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findMediaServerForMinimumLoad(deviceVo);
        if (mediaServerVo == null) {
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"流媒体未找到"));
            return result;
        }
        playService.download(sipServer,mediaServerVo,deviceVo,channelId,null,startTime,endTime,downloadSpeed,(code,msg,data)->{
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(code,msg,data));
        });
        return result;
    }

    /**
     * 停止下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param stream 流ID
     * @return
     */
    @GetMapping("/download/stop")
    public RestResult<?> stop(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("stream")String stream){
        log.info(String.format("设备历史媒体下载停止 API调用，deviceId/channelId：%s_%s", deviceGbId, channelId));
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceGbId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.streamByeCmd(sipServer, deviceVo,channelId,stream,null,null,null,null);
        }catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e){
            log.error("[停止下载录像] 发送BYE失败 {}", e.getMessage());
            return RestResult.result(RespCode.CODE_2.getValue(),"停止下载失败");
        }

        return RestResult.result(RespCode.CODE_0.getValue(),"停止成功");
    }

    /**
     * 获取当前用户下载录像信息
     */
    @GetMapping("/download/list")
    public RestResult<?> list(){
        Long userId = ObjectUtil.defaultIfNull(JwtUtils.getUserId(), VideoConstant.DEFAULT_DOWNLOAD_USER);
        return RestResult.result(RespCode.CODE_0.getValue(),null,RedisService.getInviteStreamManager().getUserDownloadInviteInfoList(userId));
    }

    /**
     * 清除用户下载录像
     */
    @DeleteMapping("/download/del")
    public RestResult<?> del(@RequestParam("key") String key){
        Long userId = ObjectUtil.defaultIfNull(JwtUtils.getUserId(), VideoConstant.DEFAULT_DOWNLOAD_USER);
        RedisService.getInviteStreamManager().delUserDownloadInviteInfoList(userId,key);
        return RestResult.result(RespCode.CODE_0.getValue(),null);
    }
}
