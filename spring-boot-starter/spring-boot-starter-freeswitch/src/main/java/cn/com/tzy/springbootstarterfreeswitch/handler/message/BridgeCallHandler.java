package cn.com.tzy.springbootstarterfreeswitch.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.BridgeCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.Constants;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 桥接处理
 * 1.先 originate 呼叫 A
 * 2.再 originate 呼叫 B
 */
@Log4j2
@Component
public class BridgeCallHandler implements FsMessageHandle {

    @Resource
    private InboundClient inboundClient;

    @Override
    public void handler(MessageModel model) {
        log.info("桥接处理参数 : model：{}",model);
        if(!(model instanceof BridgeCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"外呼消息 ：参数类型错误 应为 BridgeCallModel.class ");
        }
        BridgeCallModel bridgeCallModel = (BridgeCallModel) model;
        if(StringUtils.isEmpty(bridgeCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址 ");
        }else if(StringUtils.isEmpty(bridgeCallModel.getOneDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话通话设备 ");
        }else if(StringUtils.isEmpty(bridgeCallModel.getTwoDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话桥接设备 ");
        }
        inboundClient.sendMessage(bridgeCallModel.getMediaAddr(), new SendMsg(bridgeCallModel.getOneDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.PARK_AFTER_BRIDGE).addGenericLine("async","true"));
        inboundClient.sendMessage(bridgeCallModel.getMediaAddr(), new SendMsg(bridgeCallModel.getOneDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.HANGUP_AFTER_BRIDGE).addGenericLine("async","true"));
        inboundClient.sendMessage(bridgeCallModel.getMediaAddr(), new SendMsg(bridgeCallModel.getTwoDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.HANGUP_AFTER_BRIDGE).addGenericLine("async","true"));
        inboundClient.sendMessage(bridgeCallModel.getMediaAddr(), new SendMsg(bridgeCallModel.getTwoDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg(Constant.PARK_AFTER_BRIDGE).addGenericLine("async","true"));
        //开始桥接
        inboundClient.sendSyncApiCommand(bridgeCallModel.getMediaAddr(), Constants.UUID_BRIDGE,String.format("%s %s",bridgeCallModel.getOneDeviceId(),bridgeCallModel.getTwoDeviceId()));
    }
}
