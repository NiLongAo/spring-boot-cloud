package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.fs.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.ReceiveDtmfModel;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.SendMsg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 播放按键导航音处理
 */
@Log4j2
@Component
public class ReceiveDtmfHandler implements FsMessageHandle {
    @Resource
    private InboundClient inboundClient;
    @Override
    public void handler(MessageModel model) {
        log.info("按键导航音参数 : model：{}",model);
        if(!(model instanceof ReceiveDtmfModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"按键导航音消息 ：参数类型错误 应为 RecordCallModel.class ");
        }
        ReceiveDtmfModel receiveDtmfModel = (ReceiveDtmfModel) model;
        if(StringUtils.isEmpty(receiveDtmfModel.getMediaAddr())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话fs设备地址");
        }else if(StringUtils.isEmpty(receiveDtmfModel.getDeviceId())){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取通话设备");
        }
        //开始处理
        inboundClient.sendMessage(receiveDtmfModel.getMediaAddr(), new SendMsg(receiveDtmfModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName(Constant.SET).addExecuteAppArg("playback_delimiter=!"));
        inboundClient.sendMessage(receiveDtmfModel.getMediaAddr(), new SendMsg(receiveDtmfModel.getDeviceId()).addCallCommand(Constant.EXECUTE).addExecuteAppName("play_and_get_digits").addExecuteAppArg("3 3 2 5000 # 1295e6a58f9e2115332666.wav silence_stream://250 SYMWRD_DTMF_RETURN [\\*0-9#]+ 3000"));
    }
}
