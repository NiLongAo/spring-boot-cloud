package cn.com.tzy.springbootfs.config.socket.common.agent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentInPhoneNotificationData {

    /**
     * 用户来电操作
     * 1.挂断 2.接听
     */
    @NotNull(message = "未获取操作类型")
    private Integer type;
    @NotEmpty(message = "未获取CallId编号")
    private String callId;
    @NotNull(message = "未获取接听方式")
    private Integer onVideo;



}
