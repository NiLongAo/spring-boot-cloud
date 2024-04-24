package cn.com.tzy.springbootstarterfreeswitch.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 语音播放处理
 */
@Log4j2
@Component
public class PlayBackCallHandler implements FsMessageHandle {
    @Resource
    private InboundClient inboundClient;
    @Override
    public void handler(MessageModel model) {
        log.info("语音播放参数 : model：{}",model);
        if(!(model instanceof PlayBackCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"语音播放消息 ：参数类型错误 应为 TransferCallModel.class ");
        }
        PlayBackCallModel playBackCallModel = (PlayBackCallModel) model;
        if(StringUtils.isEmpty(playBackCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(playBackCallModel.getDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话设备");
        }
        if(!playBackCallModel.isDown()){//打开
            if(StringUtils.isEmpty(playBackCallModel.getPlayPath())){
                throw new BusinessException(RespCode.CODE_2.getValue(),"未获取语音文件地址");
            }
            inboundClient.sendMessage(playBackCallModel.getMediaAddr(), new SendMsg(playBackCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.PLAYBACK_TERMINATORS));
            inboundClient.sendMessage(playBackCallModel.getMediaAddr(), new SendMsg(playBackCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.PLAYBACK_DELIMITER));
            inboundClient.sendMessage(playBackCallModel.getMediaAddr(), new SendMsg(playBackCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.TTS_ENGINE));
            inboundClient.sendMessage(playBackCallModel.getMediaAddr(), new SendMsg(playBackCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.PLAYBACK).addExecuteAppArg(playBackCallModel.getPlayPath()));
        }else {//关闭
            inboundClient.sendMessage(playBackCallModel.getMediaAddr(),new SendMsg(playBackCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.BREAK));
        }
    }
}
