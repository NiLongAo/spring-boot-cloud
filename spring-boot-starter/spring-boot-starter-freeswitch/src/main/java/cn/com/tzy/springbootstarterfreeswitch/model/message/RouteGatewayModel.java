package cn.com.tzy.springbootstarterfreeswitch.model.message;

import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RouteGatewayModel implements MessageModel {
    /**
     * 媒体IP
     */
    private String mediaHost;
    /**
     * 媒体端口
     */
    private Integer mediaPort;
    /**
     * 主叫号码前缀
     */
    private String callerPrefix;
    /**
     * 被叫号码前缀
     */
    private String calledPrefix;
    /**
     * 媒体拨号计划文件
     */
    private String profile;
    /**
     * sip头
     */
    private List<String> sipHeaderList;

}
