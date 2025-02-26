package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.cmd;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.vo.sip.RecordInfo;
import cn.com.tzy.springbootstartervideobasic.vo.sip.RecordItem;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.ResponseMessageHandler;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.record.RecordEndSubscribeHandle;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 录像查询应答
 */
@Log4j2
public class RecordInfoResponseMessageHandler  extends SipResponseEvent implements MessageHandler {

    @Resource
    private VideoProperties videoProperties;
    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private RecordEndSubscribeHandle recordEndSubscribeHandle;

    public RecordInfoResponseMessageHandler(ResponseMessageHandler handler){
        handler.setMessageHandler(CmdType.RECORD_INFO_RESPONSE.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        try {
            // 回复200 OK
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        }catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 国标级联 国标录像: {}", e.getMessage());
        }
        ThreadUtil.execute(()->{
            try {
                String sn = XmlUtils.getText(element, "SN");
                String channelId = XmlUtils.getText(element, "DeviceID");
                RecordInfo recordInfo = new RecordInfo();
                recordInfo.setChannelId(channelId);
                recordInfo.setDeviceId(deviceVo.getDeviceId());
                recordInfo.setSn(sn);
                recordInfo.setName(XmlUtils.getText(element, "Name"));
                String sumNumStr = XmlUtils.getText(element, "SumNum");
                int sumNum = 0;
                if (!ObjectUtils.isEmpty(sumNumStr)) {
                    sumNum = Integer.parseInt(sumNumStr);
                }
                recordInfo.setSumNum(sumNum);
                Element recordModel = XmlUtil.getElement(element, "RecordList");
                List<Element> recordListElement = XmlUtil.getElements(recordModel, "Item");
                if (recordListElement.isEmpty() || sumNum == 0) {
                    log.info("无录像数据");
                    recordInfo.setCount(sumNum);
                    recordEndSubscribeHandle.handlerEvent(recordInfo);
                    releaseRequest(deviceVo.getDeviceId(), sn,recordInfo);
                } else if(! recordListElement.isEmpty()){
                    List<RecordItem> recordList = new ArrayList<>();
                    for (Element itemRecord : recordListElement) {
                        String deviceId= XmlUtils.getText(itemRecord, "DeviceID");
                        if (StringUtils.isEmpty(deviceId)) {
                            log.info("记录为空，下一个...");
                            continue;
                        }
                        RecordItem record = new RecordItem();
                        record.setDeviceId(XmlUtils.getText(itemRecord, "DeviceID"));
                        record.setName(XmlUtils.getText(itemRecord, "Name"));
                        record.setFilePath(XmlUtils.getText(itemRecord, "FilePath"));
                        record.setFileSize(XmlUtils.getText(itemRecord, "FileSize"));
                        record.setAddress(XmlUtils.getText(itemRecord, "Address"));

                        String startTimeStr = XmlUtils.getText(itemRecord, "StartTime");
                        record.setStartTime(DateUtil.formatDateTime(DateUtil.parse(startTimeStr, DatePattern.UTC_SIMPLE_PATTERN)));

                        String endTimeStr = XmlUtils.getText(itemRecord, "EndTime");
                        record.setEndTime(DateUtil.formatDateTime(DateUtil.parse(endTimeStr,DatePattern.UTC_SIMPLE_PATTERN)));

                        record.setSecrecy(StringUtils.isEmpty(XmlUtils.getText(itemRecord,"Secrecy"))? 0 : Integer.parseInt(XmlUtils.getText(itemRecord, "Secrecy")));
                        record.setType(XmlUtils.getText(itemRecord, "Type"));
                        record.setRecorderId(XmlUtils.getText(itemRecord, "RecorderID"));
                        recordList.add(record);
                    }
                    //数据有可能分次传输
                    //额外处理
                    synchronized (this){
                        String key = String.format("%s%s:%s", VideoConstant.REDIS_RECORD_INFO_RES_PRE, channelId, sn);
                        Map<String, Object> collect = recordList.stream().collect(Collectors.toMap(o -> o.getStartTime() + o.getEndTime(), o -> o,(o1,o2)->o2));
                        RedisUtils.hmset(key,collect,videoProperties.getPlayTimeout());
                        long num = RedisUtils.hmsize(key);
                        if(num < sumNum){
                            return;
                        }
                        Map<String, RecordItem> hmget =(Map<String, RecordItem>) RedisUtils.hmget(key);
                        RedisUtils.del(key);
                        List<RecordItem> recordItems = new ArrayList<>(hmget.values());
                        Collections.sort(recordItems);
                        recordInfo.setRecordList(recordItems);
                        recordInfo.setCount(Math.toIntExact(recordItems.size()));
                        recordEndSubscribeHandle.handlerEvent(recordInfo);
                        releaseRequest(deviceVo.getDeviceId(), sn,recordInfo);
                    }
                }
            } catch (Exception e) {
                log.error("[国标录像] 发现未处理的异常, \r\n{}", evt.getRequest());
                log.error("[国标录像] 异常内容： ", e);
            }
        });
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {

    }

    private void releaseRequest(String deviceId, String sn,RecordInfo recordInfo){
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_RECORDINFO,deviceId,sn);
        // 对数据进行排序
        if(recordInfo!=null && recordInfo.getRecordList()!=null) {
            Collections.sort(recordInfo.getRecordList());
        }else{
            recordInfo.setRecordList(new ArrayList<>());
        }
        deferredResultHolder.invokeAllResult(key, RestResult.result(RespCode.CODE_0.getValue(),null,recordInfo));
    }
}
