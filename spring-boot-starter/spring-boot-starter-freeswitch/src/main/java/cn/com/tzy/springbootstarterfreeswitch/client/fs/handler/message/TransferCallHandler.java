package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.TransferCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 转接电话处理
 */
@Log4j2
@Component
public class TransferCallHandler implements FsMessageHandle {

    @Resource
    private InboundClient inboundClient;
    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private PlayBackCallHandler playBackCallHandler;


    @Override
    public void handler(MessageModel model) {
        log.info("转接电话参数 : model：{}",model);
        if(!(model instanceof TransferCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"外呼消息 ：参数类型错误 应为 TransferCallModel.class ");
        }
        TransferCallModel transferCallModel = (TransferCallModel) model;
        if(StringUtils.isEmpty(transferCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(transferCallModel.getNewDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取现通话设备");
        }
        //此命令将 transferCallModel.getNewDeviceId() 转接的电话挂起，等待程序执行操作
        String format = String.format("%s -both 'set:hangup_after_bridge=false,set:park_after_bridge=true,park:' inline ", transferCallModel.getNewDeviceId());
        //发起转接
        inboundClient.sendSyncApiCommand(transferCallModel.getMediaAddr(), Constants.UUID_TRANSFER,format);
        //是否有录音文件，有就播放
        if(StringUtils.isNotBlank(transferCallModel.getOldDeviceId())){
            if(StringUtils.isNotBlank(transferCallModel.getPlayPath())){
                playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(transferCallModel.getMediaAddr()).deviceId(transferCallModel.getOldDeviceId()).playPath(transferCallModel.getPlayPath()).build());
            }
            //最后挂断原有电话
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(transferCallModel.getMediaAddr()).deviceId(transferCallModel.getOldDeviceId()).isUuidKill(false).build());
        }

    }
}
