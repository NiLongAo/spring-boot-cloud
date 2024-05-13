package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentVoInfo implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 企业ID
     */
    private String companyId;
    /**
     * 坐席账户
     */
    private String agentKey;
    /**
     * 座席密码
     */
    private String passwd;
    /**
     * 坐席工号
     */
    private String agentId;
    /**
     * 坐席分机号
     */
    private String agentCode;
    /**
     * 坐席名称
     */
    private String agentName;
    /**
     * 绑定的电话号码
     */
    private String sipPhone;
    /**
     * Sip电话号码
     */
    private List<String> sipPhoneList;
    /**
     * 座席类型：1:普通座席；2：班长
     */
    private Integer agentType;
    /**
     * 1：socket登陆
     * 2：sip登陆
     */
    private Integer loginType;
    /**
     * 是否录音 0 no 1 yes
     */
    private Integer record;
    /**
     * 座席主要技能组  不能为空 必填项
     */
    private String groupId;
    /**
     * 总机坐席
     */
    private Integer agentOnline;
    /**
     * 话后自动空闲间隔时长
     */
    private Integer afterInterval;
    /**
     * 主叫显号
     */
    private String display;
    /**
     * 振铃时长
     */
    private Integer ringTime;
    /**
     * 登录服务地址
     */
    private String host;
    /**
     * 状态：1 启用，0关闭
     */
    private Integer status;
    /**
     * 客户端地址
     */
    private String fsHost;
    /**
     * 客户端地址
     */
    private String fsPost;
    /**
     * 客户端地址
     */
    private String remoteAddress;

    /**
     * 所属技能组
     */
    private List<String> groupIds;
    /**
     * 坐席sip
     */
    private List<String> sips;
    /**
     * 坐席技能
     */
    private List<SkillAgentInfo> skillAgents;
    /**
     * 通话id
     */
    private String callId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     *  被咨询设备
     */
    private String consultDeviceId;
    /**
     * 登录时间(秒)
     */
    private Long loginTime = 0L;
    /**
     * 1:普通
     * 2:预测
     */
    private Integer workType;
    /**
     * 当前状态
     */
    private AgentStateEnum agentState;
    /**
     * 当天状态时间(秒)
     */
    private Long stateTime = 0L;
    /**
     * 坐席状态预设
     */
    private AgentPreset agentPreset;
    /**
     * 上一次状态
     */
    private AgentStateEnum beforeState;
    /**
     * 上一次状态时间(秒)
     */
    private Long beforeTime = 0L;
    /**
     * 下线时间(秒)
     */
    private Long logoutTime = 0L;
    /**
     * 坐席最近的一次服务时间,电话则是振铃时间(秒)
     */
    private Long serviceTime = 0L;
    /**
     * 最大空闲时长
     */
    private Long maxReadyTime = 0L;
    /**
     * 累计空闲
     */
    private Long totalReadyTime = 0L;
    /**
     * 空闲次数
     */
    private Long readyTimes = 0L;
    /**
     * 忙碌次数
     */
    private Long notReadyTimes = 0L;
    /**
     * 累计话后时间长
     */
    private Long totalAfterTime = 0L;
    /**
     * 最大通话时长
     */
    private Long maxTalkTime = 0L;
    /**
     * 当日累计通话时长
     */
    private Long totalTalkTime = 0L;
    /**
     * 振铃次数
     */
    private Long totalRingTimes = 0L;
    /**
     * 当日累计接听次数
     */
    private Long totalAnswerTimes = 0L;
    /**
     * 隐藏客户号码(0:不隐藏;1:隐藏)
     */
    private Integer hiddenCustomer;

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
     * 心跳周期(秒)
     */
    private Integer keepTimeout;
    /**
     * 注册时长
     */
    private int expires = 3600;
    /**
     * 传输协议 1.UDP 2.TCP
     */
    private Integer transport;
    /**
     * 数据流传输模式 0.UDP:udp传输 2.TCP-ACTIVE：tcp主动模式 2.TCP-PASSIVE：tcp被动模式
     */
    private Integer streamMode;
    /**
     * 字符集, 1.UTF-8 2.GB2312
     */
    private Integer charset= 2;

    /**
     * 获取坐席被叫号码
     *
     * @return
     */
    public String getCalled() {
        if (loginType == null) {
            return null;
        }
        if(StringUtils.isNotEmpty(this.sipPhone)){
            return this.sipPhone;
        }
        if(sipPhoneList == null || sipPhoneList.isEmpty()){
            return null;
        }
        return sipPhoneList.get(0);
    }

}
