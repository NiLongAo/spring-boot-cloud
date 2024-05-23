package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.common.fs.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.AnswerCallModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 应答电话处理
 */
@Log4j2
@Component
public class AnswerCallHandler implements FsMessageHandle {
    @Resource
    private InboundClient inboundClient;
    @Override
    public void handler(MessageModel model) {
        log.info("应答电话参数 : model：{}",model);
        if(!(model instanceof AnswerCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"应答电话消息 ：参数类型错误 应为 AnswerCallModel.class ");
        }
        AnswerCallModel answerCallModel = (AnswerCallModel) model;
        if(StringUtils.isEmpty(answerCallModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(answerCallModel.getDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话设备");
        }
        //发送应答事件
        if(answerCallModel.isActive()){
            inboundClient.sendSyncApiCommand(answerCallModel.getMediaAddr(), Constant.UUID_PHONE_EVENT,String.format("%s talk",answerCallModel.getDeviceId()));
        }else {
            inboundClient.sendMessage(answerCallModel.getMediaAddr(), new SendMsg(answerCallModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.ANSWER));
        }
    }
}
