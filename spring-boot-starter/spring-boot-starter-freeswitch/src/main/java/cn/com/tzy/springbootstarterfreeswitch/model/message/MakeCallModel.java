package cn.com.tzy.springbootstarterfreeswitch.model.message;

import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 发起呼叫相关参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MakeCallModel implements MessageModel {
    /**
     * 设备uuid
     */
    private String deviceId;

    private String callId;
    /**
     * 主叫
     */
    private String display;
    /**
     * 被叫
     */
    private String called;
    /**
     * 超时时长（秒）
     */
    private Integer originateTimeout = 15;
    /**
     * sip请求头
     */
    private List<String> sipHeaderList;
    /**
     * 网关信息
     */
    private RouteGatewayModel gatewayModel;


}
