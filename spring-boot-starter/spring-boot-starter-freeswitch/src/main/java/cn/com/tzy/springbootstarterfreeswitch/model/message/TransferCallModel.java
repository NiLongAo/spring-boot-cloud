package cn.com.tzy.springbootstarterfreeswitch.model.message;

import cn.com.tzy.springbootstarterfreeswitch.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 转接电话参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransferCallModel implements MessageModel {

    private String mediaAddr;//fs通话设备地址
    private String playPath; //录音 地址
    private String oldDeviceId;//原设备uuid
    private String newDeviceId;//现设备uuid


}
