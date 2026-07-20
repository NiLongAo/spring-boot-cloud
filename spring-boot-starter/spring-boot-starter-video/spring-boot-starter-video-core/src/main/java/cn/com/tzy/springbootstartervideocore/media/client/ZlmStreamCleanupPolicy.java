package cn.com.tzy.springbootstartervideocore.media.client;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstartervideobasic.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstartervideobasic.vo.media.OnStreamChangedResult;

final class ZlmStreamCleanupPolicy {

    static final long NEW_STREAM_GRACE_SECONDS = 120L;

    private ZlmStreamCleanupPolicy() {
    }

    static boolean shouldClose(OnStreamChangedHookVo stream,
                               OnStreamChangedResult mediaInfo,
                               boolean force,
                               long nowSeconds) {
        if (force) {
            return true;
        }
        if (isWithinGracePeriod(stream, mediaInfo, nowSeconds)) {
            return false;
        }
        return mediaInfo == null
                || mediaInfo.getCode() != RespCode.CODE_0.getValue()
                || mediaInfo.getTotalReaderCount() == null
                || mediaInfo.getTotalReaderCount() <= 0;
    }

    private static boolean isWithinGracePeriod(OnStreamChangedHookVo stream,
                                               OnStreamChangedResult mediaInfo,
                                               long nowSeconds) {
        Long aliveSecond = mediaInfo == null ? null : mediaInfo.getAliveSecond();
        if (aliveSecond != null) {
            return aliveSecond < NEW_STREAM_GRACE_SECONDS;
        }
        if (stream == null || stream.getCreateStamp() == null) {
            return true;
        }
        return nowSeconds - stream.getCreateStamp() < NEW_STREAM_GRACE_SECONDS;
    }
}
