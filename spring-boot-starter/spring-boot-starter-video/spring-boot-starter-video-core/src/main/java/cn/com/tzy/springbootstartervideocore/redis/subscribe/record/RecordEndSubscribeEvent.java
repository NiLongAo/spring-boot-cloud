package cn.com.tzy.springbootstartervideocore.redis.subscribe.record;

import cn.com.tzy.springbootstartervideobasic.vo.sip.RecordInfo;

@FunctionalInterface
public interface RecordEndSubscribeEvent {
        void  handler(RecordInfo recordInfo);
}