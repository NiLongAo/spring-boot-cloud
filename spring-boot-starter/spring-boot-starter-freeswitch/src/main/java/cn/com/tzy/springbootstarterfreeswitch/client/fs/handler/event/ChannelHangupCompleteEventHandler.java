package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallDeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogPush;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.utils.PushUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  挂机完成事件
 */
@Log4j2
@Component
@EslEventName(EventNames.CHANNEL_HANGUP_COMPLETE)
public class ChannelHangupCompleteEventHandler implements EslEventHandler {
    @Resource
    private HangupCallHandler hangupCallHandler;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 CHANNEL_HANGUP_COMPLETE");
        String uniqueId = EslEventUtil.getUniqueId(event);
        String hangupCause = event.getEventHeaders().get("Hangup-Cause");//挂机原因
        String sipProtocol = event.getEventHeaders().get("variable_sip_via_protocol");//sip信令协议
        String sipStatus = event.getEventHeaders().get("variable_sip_term_status");//sip状态
        String channelName = event.getEventHeaders().get("variable_channel_name");//sip呼叫地址
        String localMediaIp = event.getEventHeaders().get("variable_local_media_ip");//fs 服务地址
        String eventDateTimestamp = EslEventUtil.getEventDateTimestamp(event);//接通时间（毫秒值）
        Date answerTime = new Date();
        if(eventDateTimestamp != null){
            answerTime = DateUtil.date(Long.parseLong(eventDateTimestamp));
        }
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if (callInfo == null) {
            return;
        }
        int count = callInfo.getDeviceList().size();
        callInfo.getDeviceList().remove(uniqueId);
        // 挂机原因
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(uniqueId);
        if (deviceInfo == null) {
            log.warn("device:{} is null, callId:{}", uniqueId, callInfo.getCallId());
            return;
        }
        deviceInfo.setHangupCause(hangupCause);
        deviceInfo.setSipProtocol(sipProtocol);
        deviceInfo.setSipStatus(sipStatus);
        deviceInfo.setChannelName(channelName);
        deviceInfo.setEndTime(answerTime);
        //计算录音时长
        if (deviceInfo.getRecordStartTime() != null) {
            deviceInfo.setRecordTime(DateUtil.between(deviceInfo.getRecordStartTime(), deviceInfo.getEndTime(), DateUnit.SECOND));
        }
        log.info("callId:{} deviceId:{} deviceType:{} display:{} called:{} sipStatus:{} sipProtocol:{} hangupCause:{}", callInfo.getCallId(), deviceInfo.getDeviceId(), deviceInfo.getDeviceType(), deviceInfo.getDisplay(), deviceInfo.getCalled(), sipStatus, sipProtocol, hangupCause);
        // 上传录音文件 自动与minio文件映射地址同步无需关联
//        if (StringUtils.isNotBlank(deviceInfo.getRecord())) {
//            String day = DateFormatUtils.format(new Date(), "yyyyMMddHH");
//            String[] record = deviceInfo.getRecord().split("/");
//            String fileName = "/" + day.substring(0, 6) + "/" + day.substring(0, 8) + "/" + day.substring(8, 10) + "/" + record[record.length - 1];
//            String url = "http://" + localMediaIp + ":7430" + deviceInfo.getRecord();
//            try {
//                ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
//                logger.info("get record file:{}", deviceInfo.getRecord());
//                ObjectWriteResponse writeResponse = minioClient.putObject(PutObjectArgs.builder().stream(new ByteArrayInputStream(responseEntity.getBody()), responseEntity.getBody().length, -1).object(fileName).bucket("cc-record").build());
//                logger.info("callId:{}, record fileName:{}, minioTag:{}", deviceInfo.getCallId(), fileName, writeResponse.etag());
//                deviceInfo.setRecord("/cc-record" + fileName);
//            } catch (Exception e) {
//                log.error("url:" + url + e.getMessage(), e);
//            }
//        }
        CallDeviceInfo callDevice = new CallDeviceInfo();
        BeanUtils.copyProperties(deviceInfo, callDevice);
        callDevice.setCreateTime(deviceInfo.getEndTime());
        callDevice.setUpdateTime(callDevice.getEndTime());
        callDevice.setCompanyId(callInfo.getCompanyId());
        FsService.getCallCdrService().saveCallDevice(callDevice);
        callInfo.getDeviceInfoMap().put(callDevice.getDeviceId(), deviceInfo);
        // 一般情况下，挂断其他所有设备
        if (deviceInfo.getCdrType() <= 3 && callInfo.getEndTime() == null) {
            if (!CollectionUtils.isEmpty(callInfo.getDeviceList())) {
                callInfo.getDeviceList().forEach(s -> {
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(s).build());
                });
            }
        }
        RedisService.getCallInfoManager().put(callInfo);
        //最后一个设备挂机
        if (count == 1) {
            hangup(callInfo);
        }

    }

    /**
     * 全部挂机
     *
     * @param callInfo
     */
    private void hangup(CallInfo callInfo) {
        List<CallDeviceInfo> callDeviceList = new ArrayList<>();
        //call_device
        callInfo.getDeviceInfoMap().forEach((k, v) -> {
            CallDeviceInfo callDevice = new CallDeviceInfo();
            BeanUtils.copyProperties(v, callDevice);
            callDeviceList.add(callDevice);

        });

        CallLogInfo callLog = new CallLogInfo();
        BeanUtils.copyProperties(callInfo, callLog);
        callLog.setCreateTime(callInfo.getCallTime());
        callLog.setUpdateTime(callInfo.getEndTime());
        callLog.setEndTime(callInfo.getEndTime());
        //防止跨月份落单
        callLog.setMonthTime(DateUtil.format(callLog.getCallTime(), DatePattern.SIMPLE_MONTH_PATTERN));

        callLog.setCallType(callInfo.getCallType().name());
        callLog.setDirection(callInfo.getDirection().name());
        if (!CollectionUtils.isEmpty(callInfo.getFollowData())) {
            callLog.setFollowData(AppUtils.encodeJson2(callInfo.getFollowData()));
        }
        if (callInfo.getAnswerTime() != null) {
            callLog.setTalkTime(DateUtil.between(callInfo.getAnswerTime(),callInfo.getEndTime(), DateUnit.SECOND));
            callInfo.setTalkTime(callLog.getTalkTime());
            callInfo.setAnswerFlag(0);
        }
        FsService.getCallCdrService().saveCallDetail(callInfo.getCallDetails());
        FsService.getCallCdrService().saveOrUpdateCallLog(callLog);

        CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(callInfo.getCompanyId());

        //清空电话信息
        RedisService.getCallInfoManager().del(callInfo.getCallId());
        String notifyUrl = companyInfo.getNotifyUrl();
        if (StringUtils.isBlank(notifyUrl)) {
            return;
        }
        //话单推送
        CallLogPush callLogPo = new CallLogPush();
        BeanUtils.copyProperties(callLog, callLogPo);
        callLogPo.setCallDeviceList(callDeviceList);
        callLogPo.setCaller(callInfo.getCaller());
        callLogPo.setCalled(callInfo.getCalled());
        RestResult<?> request = PushUtils.request(notifyUrl, callLogPo);
        if(request.getCode() != RespCode.CODE_0.getValue()){
            log.error("push call:{} to {} error, payload:{}", callInfo.getCallId(), notifyUrl, AppUtils.encodeJson2(callLogPo));
        }else {
            log.info("push call:{} to {} success, statusCode:{} response:{}", callInfo.getCallId(), notifyUrl, request.getCode(), AppUtils.encodeJson2(request));
        }
    }
}
