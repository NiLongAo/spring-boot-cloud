package cn.com.tzy.springbootstarterfreeswitch.utils;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogPush;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PushUtils {
    /**
     * 推送相关数据
     * @return
     */
    public static RestResult request(String url, CallLogPush param){
        RestResult execute ;
        try {
            ForestRequest<?> forestRequest = Forest
                    .post(url)
                    .contentType(ConstEnum.ContentType.JSON.getValue())
                    .readTimeout(10000)
                    .addBody(param);
            execute = forestRequest.execute(RestResult.class);
        }catch (Exception e){
            log.error("推送数据时 发生错误 : ",e);
            execute = RestResult.result(RespCode.CODE_2.getValue(),"推送数据错误");
        }
        return execute;
    }
}
