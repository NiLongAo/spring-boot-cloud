package cn.com.tzy.springbootsms.pool.job;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.sms.PublicNotice;
import cn.com.tzy.springbootsms.config.socket.publicMessage.common.MessageType;
import cn.com.tzy.springbootsms.config.socket.publicMessage.event.PublicMemberEvent;
import cn.com.tzy.springbootsms.config.socket.publicMessage.namespace.PublicMemberNamespace;
import cn.com.tzy.springbootsms.service.PublicNoticeService;
import cn.com.tzy.springbootsms.service.ReadNoticeUserService;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.common.OutType;
import cn.com.tzy.springbootstartersocketio.message.Message;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class PubLicJob {

    @Autowired
    private PublicNoticeService publicNoticeService;
    @Autowired
    private ReadNoticeUserService readNoticeUserService;
    @Autowired
    private PublicMemberNamespace publicMemberNamespace;
    @Autowired
    private PublicMemberEvent publicMemberEvent;


    @XxlJob("pubLicMessageHandler")
    public void demoJobHandler() throws Exception {
        try {
            log.info("检测未发送平台公告用户并发送。。。。开始");
            Date date = new Date();
            List<PublicNotice> dateRange = publicNoticeService.findDateRange(date);
            for (PublicNotice publicNotice : dateRange) {
                List<Long> userIdList = publicMemberNamespace.getRoomId(String.format("%s:", Constant.USER_ID_KEY));
                List<Long> copyUserIdList= new ArrayList<>(userIdList);
                List<Long> collect = new ArrayList<>();
                //删除当天发送用户
                String key = Constant.PUBLIC_NOTICE_USER_LIST+ DateFormatUtils.format(date,Constant.DATE_FORMAT) + publicNotice.getId();
                if(RedisUtils.hasKey(key)){
                    List<Object> objects = RedisUtils.lGet(key, 0, -1);
                    collect = objects.stream().map(obj -> Long.valueOf(String.valueOf(obj))).collect(Collectors.toList());
                }
                if(!collect.isEmpty()){
                    copyUserIdList.removeAll(collect);
                }
                if(copyUserIdList.isEmpty()){
                    continue;
                }
                List<Long> noticeIdCount = readNoticeUserService.findNoticeIdCount(publicNotice.getId());
                //再删除掉已读用户
                if(!noticeIdCount.isEmpty()){
                    copyUserIdList.removeAll(noticeIdCount);
                }
                if(copyUserIdList.isEmpty()){
                    continue;
                }
                Message message = Message.builder()
                        .code(Message.Code.SUCCESS.getValue())
                        .message("获取成功")
                        .data(new NotNullMap(){{
                            putInteger("type",MessageType.PUBLIC_NOTICE.getValue());
                            putInteger("outType",OutType.MESSAGE.getValue());
                            putInteger("userId",0);
                            putString("userName","系统");
                            putString("message",publicNotice.getTitle());
                            putDateTime("createTime",new Date());
                        }})
                        .build();
                //直接发送socket消息
                publicMemberEvent.send(copyUserIdList, Constant.USER_ID_KEY, message);
                Date truncate = DateUtils.addDays(DateUtils.truncate(date, Calendar.DAY_OF_MONTH),1);
                for (Long userId : copyUserIdList) {
                    RedisUtils.lSet(key,userId,(int)((truncate.getTime()-date.getTime())/1000));
                }
            }
        }catch (Exception e){
            log.error("推送平台公告消息 失败：",e);
        }
        log.info("检测未发送平台公告用户并发送。。。。结束");

    }
}
