package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SsrcConfigManager {

    @Resource
    private SipServer sipServer;
    @Resource
    private VideoProperties videoProperties;

    /**
     * 播流最大并发个数
     */
    private static final Integer MAX_STREAM_COUNT = 10000;
    /**
     * 播流最大并发个数
     */
    private String SSRC_CONFIG_INFO_PREFIX = VideoConstant.SSRC_CONFIG_INFO_PREFIX;

    public void initMediaServerSSRC(String mediaServerId, Set<String> usedSet) {
        SipConfigProperties deviceSipConfig = sipServer.getSipConfigProperties();
        String ssrcPrefix = deviceSipConfig.getDomain().substring(3, 8);
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        if (!hasMediaServerSSRC(mediaServerId)) {
            RedisUtils.del(redisKey);
        }
        List<String> ssrcList = new ArrayList<>();
        for (int i = 1; i < MAX_STREAM_COUNT; i++) {
            String ssrc = String.format("%s%04d", ssrcPrefix, i);
            if (null == usedSet || !usedSet.contains(ssrc)) {
                ssrcList.add(ssrc);
            }
        }
        RedisUtils.sSet(redisKey, ssrcList.toArray());
    }

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     * @return ssrc
     */
    public String getPlaySsrc(String mediaServerId) {
        return "0" + getSN(mediaServerId);
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     */
    public String getPlayBackSsrc(String mediaServerId) {
        return "1" + getSN(mediaServerId);
    }

    /**
     * 获取后四位数SN,随机数
     */
    private String getSN(String mediaServerId) {
        String sn = null;
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        Long size = RedisUtils.sGetSetSize(redisKey);
        if (size == null || size == 0) {
            throw new RuntimeException("ssrc已经用完");
        } else {
            // 在集合中移除并返回一个随机成员。
            sn = (String) RedisUtils.sPop(redisKey);
            RedisUtils.setRemove(redisKey, sn);
        }
        return sn;
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     * @param ssrc 需要重置的ssrc
     */
    public void releaseSsrc(String mediaServerId, String ssrc) {
        if (ssrc == null) {
            return;
        }
        String sn = ssrc.substring(1);
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        RedisUtils.sSet(redisKey, sn);
    }

    /**
     * 重置一个流媒体服务的所有ssrc
     *
     * @param mediaServerId 流媒体服务ID
     */
    public void reset(String mediaServerId, String sipDomain) {
        this.initMediaServerSSRC(mediaServerId, null);
    }

    /**
     * 是否已经存在了某个MediaServer的SSRC信息
     *
     * @param mediaServerId 流媒体服务ID
     */
    public boolean hasMediaServerSSRC(String mediaServerId) {
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        return RedisUtils.hasKey(redisKey);
    }

    /**
     * 删除缓存
     *
     * @param mediaServerId 流媒体服务ID
     */
    public void del(String mediaServerId) {
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        if (!hasMediaServerSSRC(mediaServerId)) {
            RedisUtils.del(redisKey);
        }
    }

    /**
     * 查询ssrc是否可用
     * @param mediaServerId
     * @param ssrc
     * @return
     */
    public boolean checkSsrc(String mediaServerId, String ssrc) {
        String sn = ssrc.substring(1);
        String redisKey = String.format("%s%s:%s",SSRC_CONFIG_INFO_PREFIX,videoProperties.getServerId(),mediaServerId);
        return RedisUtils.sHasKey(redisKey, sn) != null && RedisUtils.sHasKey(redisKey, sn);
    }

}
