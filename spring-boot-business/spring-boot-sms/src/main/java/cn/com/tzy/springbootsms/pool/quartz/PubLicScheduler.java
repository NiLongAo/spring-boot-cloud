//package cn.com.tzy.springbootsms.pool;
//
//import cn.com.tzy.springbootcomm.constant.Constant;
//import cn.com.tzy.springbootentity.dome.sms.PublicNotice;
//import cn.com.tzy.springbootsms.config.socket.publicMessage.common.MessageType;
//import cn.com.tzy.springbootsms.config.socket.publicMessage.event.PublicMemberMessage;
//import cn.com.tzy.springbootsms.config.socket.publicMessage.namespace.PublicMemberNamespace;
//import cn.com.tzy.springbootsms.service.PublicNoticeService;
//import cn.com.tzy.springbootsms.service.ReadNoticeUserService;
//import cn.com.tzy.springbootstarterquartz.config.task.QuartzTaskJob;
//import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
//import cn.com.tzy.springbootstartersocketio.common.OutType;
//import cn.com.tzy.springbootsms.config.socket.publicMessage.common.PublicMessage;
//import com.sun.javafx.binding.StringConstant;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.apache.commons.lang3.time.DateUtils;
//import org.quartz.JobExecutionContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Order(1)
//@Component
//public class PubLicScheduler implements QuartzTaskJob {
//
//    @Autowired
//    private PublicNoticeService publicNoticeService;
//    @Autowired
//    private ReadNoticeUserService readNoticeUserService;
//    @Autowired
//    private PublicMemberNamespace publicMemberNamespace;
//    @Autowired
//    private PublicMemberMessage publicMemberMessage;
//    /**
//     * 自定义 quartz 定时器关闭 启用 xxl-job第三方定时器
//     */
//    
//    /**
//     * 向所有已在线用户推送平台公告消息，已读并当天已推送不会再二次推送
//     */
//    @Override
//    public void execute(JobExecutionContext context) {
//        try {
//            log.info("检测未发送平台公告用户并发送。。。。开始");
//            Date date = new Date();
//            List<PublicNotice> dateRange = publicNoticeService.findDateRange(date);
//            for (PublicNotice publicNotice : dateRange) {
//                List<Long> userIdList = publicMemberNamespace.getRoomId(String.format("%s:",Constant.USER_ID_KEY));
//                List<Long> copyUserIdList= new ArrayList<>(userIdList);
//                List<Long> collect = new ArrayList<>();
//                //删除当天发送用户
//                String key = Constant.PUBLIC_NOTICE_USER_LIST+ DateFormatUtils.format(date,Constant.DATE_FORMAT) + publicNotice.getId();
//                if(RedisUtils.hasKey(key)){
//                    List<Object> objects = RedisUtils.lGet(key, 0, -1);
//                    collect = objects.stream().map(obj -> Long.valueOf(String.valueOf(obj))).collect(Collectors.toList());
//                }
//                if(!collect.isEmpty()){
//                    copyUserIdList.removeAll(collect);
//                }
//                if(copyUserIdList.isEmpty()){
//                    continue;
//                }
//                List<Long> noticeIdCount = readNoticeUserService.findNoticeIdCount(publicNotice.getId());
//                //再删除掉已读用户
//                if(!noticeIdCount.isEmpty()){
//                    copyUserIdList.removeAll(noticeIdCount);
//                }
//                if(copyUserIdList.isEmpty()){
//                    continue;
//                }
//                //直接发送socket消息
//                 publicMemberMessage.send(
//                        copyUserIdList,
//                        Constant.USER_ID_KEY,
//                        PublicMessage.builder().type(MessageType.PUBLIC_NOTICE.getValue()).outType(OutType.MESSAGE.getValue())
//                        .userId(String.valueOf(0)).userName("系统").message(publicNotice.getTitle())
//                        .createTime(new Date()).build()
//                );
//                Date truncate = DateUtils.addDays(DateUtils.truncate(date, Calendar.DAY_OF_MONTH),1);
//                for (Long userId : copyUserIdList) {
//                    RedisUtils.lSet(key,userId,(int)((truncate.getTime()-date.getTime())/1000));
//                }
//                //redis发布消息
//                //redisTemplate.convertAndSend(RedisCommon.WEB_REDIS_MESSAGE_EVENT, AppUtils.encodeJson(inMessage));
//            }
//        }catch (Exception e){
//            log.error("推送平台公告消息 失败：",e);
//        }
//        log.info("检测未发送平台公告用户并发送。。。。结束");
//    }
//
//}
