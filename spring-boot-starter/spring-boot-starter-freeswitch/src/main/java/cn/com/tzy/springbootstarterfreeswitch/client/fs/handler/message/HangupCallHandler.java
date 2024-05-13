package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.fs.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 挂断电话处理
 */
@Log4j2
@Component
public class HangupCallHandler implements FsMessageHandle {

    @Resource
    private InboundClient inboundClient;

    @Override
    public void handler(MessageModel model) {
        log.info("挂断电话参数 : model：{}",model);
        if(!(model instanceof HangupCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"挂断电话消息 ：参数类型错误 应为 HangupCallModel.class ");
        }
        HangupCallModel hangupCallModel = (HangupCallModel) model;
        if(StringUtils.isEmpty(hangupCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(hangupCallModel.getDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话设备");
        }
        if(hangupCallModel.isUuidKill()){
            inboundClient.sendMessage(hangupCallModel.getMediaAddr(), new SendMsg(hangupCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.HANGUP).addExecuteAppArg(Constant.NORMAL_CLEARING));
        }else {
            inboundClient.sendSyncApiCommand(hangupCallModel.getMediaAddr(),Constant.UUID_KILL,hangupCallModel.getDeviceId());
        }
    }
}
