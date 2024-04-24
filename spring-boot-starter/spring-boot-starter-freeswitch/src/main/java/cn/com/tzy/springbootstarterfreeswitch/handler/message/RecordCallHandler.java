package cn.com.tzy.springbootstarterfreeswitch.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.RecordCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.Constants;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 录音处理
 */
@Log4j2
@Component
public class RecordCallHandler implements FsMessageHandle {
    @Resource
    private InboundClient inboundClient;
    @Override
    public void handler(MessageModel model) {
        log.info("录音参数 : model：{}",model);
        if(!(model instanceof RecordCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"录音消息 ：参数类型错误 应为 TransferCallModel.class ");
        }
        RecordCallModel recordCallModel = (RecordCallModel) model;
        if(StringUtils.isEmpty(recordCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(recordCallModel.getDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话设备");
        }else if(StringUtils.isEmpty(recordCallModel.getPlayPath())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取录音地址");
        }
        //设置8kHz采样率
        inboundClient.sendMessage(recordCallModel.getMediaAddr(), new SendMsg(recordCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.RECORD_SAMPLE_RATE+recordCallModel.getSampleRate()));
        //双声道录音,默认是单声道录音
        inboundClient.sendSyncApiCommand(recordCallModel.getMediaAddr(), Constants.UUID_SETVAR,String.format("%s%s",recordCallModel.getDeviceId(),Constant.RECORD_STEREO));
        //开始录音
        inboundClient.sendSyncApiCommand(recordCallModel.getMediaAddr(), Constants.UUID_RECORD,String.format("%s %s %s", recordCallModel.getDeviceId(), Constant.START, recordCallModel.getPlayPath()));
    }
}
