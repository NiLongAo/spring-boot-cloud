package cn.com.tzy.springbootstarterfreeswitch.handler.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstarterfreeswitch.common.Constant;
import cn.com.tzy.springbootstarterfreeswitch.exception.BusinessException;
import cn.com.tzy.springbootstarterfreeswitch.handler.FsMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.MakeCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.RouteGatewayModel;
import cn.com.tzy.springbootstarterfreeswitch.utils.FreeswitchUtils;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 外呼消息处理
 */
@Log4j2
@Component
public class MakeCallHandler implements FsMessageHandle {

    @Resource
    private InboundClient inboundClient;

    private final static String codecs = "^^:G729:PCMU:PCMA";

    @Override
    public void handler(MessageModel model) {
        if(!(model instanceof MakeCallModel)){
            throw new BusinessException(RespCode.CODE_2.getValue(),"外呼消息 ：参数类型错误 应为 MakeCallModel.class ");
        }
        MakeCallModel makeCallModel = (MakeCallModel) model;
        //网关信息
        RouteGatewayModel gatewayModel = makeCallModel.getGatewayModel();
        if(gatewayModel == null){
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取发送 未获取网关信息 ");
        }
        if (StringUtils.isBlank(gatewayModel.getMediaHost())) {
            throw new BusinessException(RespCode.CODE_2.getValue(),"未获取发送 host ");
        }
        String display = makeCallModel.getDisplay();//主叫
        String called = makeCallModel.getCalled() + Constant.AT + gatewayModel.getMediaHost() + Constant.CO + gatewayModel.getMediaPort ();//被叫
        if (StringUtils.isNotBlank(gatewayModel.getCallerPrefix())) {
            display = gatewayModel.getCallerPrefix() + display;
        }
        if (StringUtils.isNotBlank(gatewayModel.getCalledPrefix())) {
            called = gatewayModel.getCalledPrefix() + called;
        }
        //添加传入sip请求头
        List<String> sipHeaders = new ArrayList<>();
        if(makeCallModel.getSipHeaderList() != null && !makeCallModel.getSipHeaderList().isEmpty()){
            sipHeaders.addAll(makeCallModel.getSipHeaderList());
        }
        //添加网关的sip请求头
        if(gatewayModel.getSipHeaderList() != null && !gatewayModel.getSipHeaderList().isEmpty()){
            sipHeaders.addAll(gatewayModel.getSipHeaderList());
        }

        Map<String, Object> sipParams = new HashMap<>();
        sipParams.put("callId", makeCallModel.getCallId());//暂不知道作用
        sipParams.put("deviceId",makeCallModel.getDeviceId());//暂不知道作用
        sipParams.put("caller", display);
        sipParams.put("called", called);

        Map<String, Object> fsParams=new HashMap<>();
        fsParams.put("return_ring_ready",true);
        fsParams.put("sip_contact_user",display);
        fsParams.put("ring_asr",true);
        fsParams.put("absolute_codec_string",codecs);
        fsParams.put("origination_caller_id_number",display);
        fsParams.put("origination_caller_id_name",display);
        fsParams.put("origination_uuid",makeCallModel.getDeviceId());
        if(makeCallModel.getOriginateTimeout() !=null){
            fsParams.put("originate_timeout", makeCallModel.getOriginateTimeout());
        }
        //添加sip请求头信息
        if(!sipHeaders.isEmpty()){
            Map<String, String> collect = sipHeaders.stream().filter(StringUtils::isNotBlank).map(o -> Constant.SIP_HEADER + FreeswitchUtils.expression(o, sipParams)).collect(Collectors.toMap(o -> o.split(Constant.EQ)[0], o -> o.split(Constant.EQ)[1]));
            fsParams.putAll(collect);
        }
        StringBuffer builder = new StringBuffer();
        builder.append(AppUtils.encodeJson2(fsParams));
        builder.append(Constant.SOFIA + Constant.SK).append(gatewayModel.getProfile()).append(Constant.SK);
        builder.append(called);
        builder.append(Constant.PARK);
        //随机获取一个Fs链接地址
        String addr = inboundClient.option().serverAddrOption().random();
        log.info("外呼消息处理 发送参数: addr：{}，command：{}，arg：{}",addr,Constant.ORIGINATE,builder.toString());
        EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, Constant.ORIGINATE, builder.toString());
        log.info("外呼消息处理 响应:{}",eslMessage.toString());
    }
}
