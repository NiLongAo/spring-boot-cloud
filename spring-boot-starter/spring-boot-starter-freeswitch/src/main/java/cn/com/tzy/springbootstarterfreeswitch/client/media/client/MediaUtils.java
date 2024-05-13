package cn.com.tzy.springbootstarterfreeswitch.client.media.client;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.ZLMediaKitConstant;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class MediaUtils {

    /**
     * 请求流媒体服务
     * @param mediaServerVo 流媒体信息
     * @param param 请求参数
     * @return
     */
    public static MediaRestResult request(MediaServerVo mediaServerVo, String url, Object... param){
        MediaRestResult execute ;
        try {
            ForestRequest<?> forestRequest = Forest
                    .get(StringUtils.isEmpty(mediaServerVo.getVideoHttpPrefix())?url:String.format("/%s%s",mediaServerVo.getVideoHttpPrefix(),url))
                    .setScheme(mediaServerVo.getSslStatus()== ConstEnum.Flag.YES.getValue()?"https":"http")
                    .readTimeout(10000)
                    .host(String.format("%s:%s", mediaServerVo.getIp(), mediaServerVo.getSslStatus()== ConstEnum.Flag.YES.getValue() ?mediaServerVo.getHttpSslPort():mediaServerVo.getHttpPort()))
                    .addQuery(ZLMediaKitConstant.MEDIA_SECRET, mediaServerVo.getSecret());
            for (Object obj: param) {
                forestRequest.addQuery(obj);
            }
            execute = forestRequest.execute(MediaRestResult.class);
        }catch (Exception e){
            log.error("请求流媒体是 发生错误 : ",e);
            execute = MediaRestResult.result(RespCode.CODE_2.getValue(),"请求流媒体错误");
        }
        return execute;
    }

    /**
     * 请求流媒体服务
     * @param mediaServerVo 流媒体信息
     * @param param 请求参数
     * @return
     */
    public static OnStreamChangedResult requestStreamChanged(MediaServerVo mediaServerVo, String url, Object... param){
        OnStreamChangedResult execute ;
        try {
            ForestRequest<?> forestRequest = Forest
                    .get(StringUtils.isEmpty(mediaServerVo.getVideoHttpPrefix())?url:String.format("/%s%s",mediaServerVo.getVideoHttpPrefix(),url))
                    .setScheme(mediaServerVo.getSslStatus()== ConstEnum.Flag.YES.getValue()?"https":"http")
                    .readTimeout(10000)
                    .host(String.format("%s:%s", mediaServerVo.getIp(), mediaServerVo.getSslStatus()== ConstEnum.Flag.YES.getValue() ?mediaServerVo.getHttpSslPort():mediaServerVo.getHttpPort()))
                    .addQuery(ZLMediaKitConstant.MEDIA_SECRET, mediaServerVo.getSecret());
            for (Object obj: param) {
                forestRequest.addQuery(obj);
            }
            execute = forestRequest.execute(OnStreamChangedResult.class);
        }catch (Exception e){
            log.error("请求流媒体是 发生错误 : ",e);
            execute = OnStreamChangedResult.result(RespCode.CODE_2.getValue());
        }
        return execute;
    }
}
