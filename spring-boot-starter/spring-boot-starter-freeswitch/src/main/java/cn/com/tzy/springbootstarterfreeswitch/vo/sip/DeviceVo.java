package cn.com.tzy.springbootstarterfreeswitch.vo.sip;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 允许注册的设备信息
 */
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVo extends LongIdEntity {
    /**
     * 设备国标编号
     */
    private String deviceId;

    /**
     * 设备名
     */
    private String name;

    /**
     * 传输协议 1.UDP 2.TCP
     */
    private Integer transport;

    /**
     * wan地址_ip
     */
    private String ip;

    /**
     * wan地址_port
     */
    private Integer port;

    /**
     * wan地址
     */
    private String hostAddress;

    /**
     * 设备密码
     */
    private String password;

    /**
     * 收流IP
     */
    private String sdpIp;

    /**
     * SIP交互IP（设备访问平台的IP）
     */
    private String localIp;

    /**
     * 字符集, 1.UTF-8 2.GB2312
     */
    private Integer charset= 2;

    /**
     * 是否在线，1.为在线，0.为离线
     */
    private Integer online= 0;

    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 续订时间
     */
    private Date renewTime;
    /**
     * 心跳时间
     */
    private Date keepaliveTime;

    /**
     * 心跳间隔
     */
    private Integer keepaliveIntervalTime= 30;

    /**
     * 数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式
     */
    private Integer streamMode = 0;

    /**
     * 注册有效期
     */
    private Integer expires;

    /**
     * 是否开启ssrc校验，默认关闭，开启可以防止串流
     */
    private Integer ssrcCheck = 0;


    public boolean expire(){
        if(renewTime == null || expires == null){
            return true;
        }
        DateTime endTime = DateUtil.offsetSecond(renewTime, expires+ SipConstant.DELAY_TIME);
        return endTime.isBefore(new Date());
    }

    public boolean keepalive(){
        if(keepaliveTime == null || keepaliveIntervalTime == null){
            return true;
        }
        DateTime endTime = DateUtil.offsetSecond(keepaliveTime, keepaliveIntervalTime+ SipConstant.DELAY_TIME);
        return endTime.isBefore(new Date());
    }

}