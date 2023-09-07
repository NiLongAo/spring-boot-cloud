package cn.com.tzy.springbootstartervideobasic.vo.sip;

import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteStreamInfo {

    private MediaServerVo mediaServerVoItem;
    private JSONObject response;
    private String callId;
    private String app;
    private String stream;
}
