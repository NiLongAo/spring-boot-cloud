package cn.com.tzy.springbootstarterfreeswitch.config.zlm;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.ZlmService;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.MediaServerManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.ZLMServerConfig;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备启动时检测流媒体服务
 */
@Log4j2
@Order(30)
@Component
public class ZLMRunner implements CommandLineRunner {

    @Resource
    private MediaHookSubscribe mediaHookSubscribe;
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private ZlmService zlmService;

    private List<String> allZML;

    @Override
    public void run(String... args) throws Exception {
        MediaServerManager mediaServerManager = RedisService.getMediaServerManager();
        //清除缓存
        mediaServerManager.clearMediaServerForOnline();
        //订阅zlm启动事件
        HookKey hookKey = HookKeyFactory.onServerStarted();
        mediaHookSubscribe.addSubscribe(hookKey,(MediaServerVo mediaServerVo, HookVo response)->{
            if(response instanceof ZLMServerConfig){
                ZLMServerConfig config = (ZLMServerConfig) response;
                if(allZML != null){
                    allZML.remove(config.getGeneralMediaServerId());
                }
            }
        });
        log.info("[ZML] 启动中  每5分钟定时检测......]");
        //动态检测是否有空闲流媒体 5分钟检测
        dynamicTask.startCron("zlm-connect-5m",1,300,()->detection(hookKey));
    }
    //动态检测是否有空闲流媒体
    private void detection(HookKey hookKey){
        List<MediaServerVo> serviceAll= SipService.getMediaServerService().findConnectZlmList();
        allZML = serviceAll.stream().map(MediaServerVo::getId).collect(Collectors.toList());
        if(allZML.isEmpty()){
            log.warn("[ZML] 没有空闲流媒体需注册");
            return;
        }
        dynamicTask.stop("zlm-connect-timeout");
        //30秒未检测到流媒体启动，则放弃
        dynamicTask.startDelay("zlm-connect-timeout",40,()->{
            if (allZML != null && !allZML.isEmpty()) {
                for (String id : allZML) {
                    log.error("[ {} ]]主动连接失败，不再尝试连接", id);
                }
                allZML = null;
            }
            //mediaHookSubscribe.removeSubscribe(hookKey);
        });
        for (MediaServerVo mediaServerVo : serviceAll) {
            ThreadUtil.execute(()->connectZlmServer(mediaServerVo));
        }
    }


    //连接服务
    private void connectZlmServer(MediaServerVo mediaServerVo){
        ZLMServerConfig config = MediaClient.getZLMServerConfig(mediaServerVo);
        if(config == null){
            log.error("[ {} ]-[ {}:{} ]主动连接失败 ", mediaServerVo.getId(), mediaServerVo.getIp(), mediaServerVo.getHttpPort());
            return;
        }
        config.setIp(mediaServerVo.getIp());
        config.setHttpPort(mediaServerVo.getHttpPort());
        allZML.remove(mediaServerVo.getId());
        if (allZML.size() == 0) {
            mediaHookSubscribe.removeSubscribe(HookKeyFactory.onServerStarted());
        }
        config.setRestart("0".equals(config.getHookEnable())? ConstEnum.Flag.YES.getValue() :ConstEnum.Flag.NO.getValue());
        zlmService.zlmOnline(config);
    }



}

