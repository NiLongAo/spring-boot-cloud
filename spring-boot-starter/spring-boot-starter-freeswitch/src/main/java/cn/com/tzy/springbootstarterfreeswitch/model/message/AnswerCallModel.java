package cn.com.tzy.springbootstarterfreeswitch.model.message;

import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 *应答参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AnswerCallModel implements MessageModel {

    private boolean isActive;// 是否主动应答
    private String mediaAddr;//fs通话设备地址 主动
    private String deviceId;//设备

}
