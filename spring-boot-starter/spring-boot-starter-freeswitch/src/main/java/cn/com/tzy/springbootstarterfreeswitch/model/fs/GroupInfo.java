package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 技能组
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfo implements Serializable {

    private String id;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 技能组名称
     */
    private String name;

    /**
     * 话后自动空闲时长
     */
    private Integer afterInterval;

    /**
     * 主叫显号号码池
     */
    private Long callerDisplayId;

    /**
     * 被叫显号号码池
     */
    private Long calledDisplayId;

    /**
     * 0:不录音,1:振铃录音,2:接通录音
     */
    private Integer recordType;

    /**
     * 技能组优先级
     */
    private Integer levelValue;

    /**
     *
     */
    private Long ttsEngine;

    /**
     * 转坐席时播放内容
     */
    private String playContent;

    /**
     * 转服务评价(0:否,1:是)
     */
    private Long evaluate;

    /**
     * 排队音
     */
    private Long queuePlay;

    /**
     * 转接提示音
     */
    private Long transferPlay;

    /**
     * 外呼呼叫超时时间
     */
    private Integer callTimeOut;

    /**
     * 技能组类型
     */
    private Integer groupType;

    /**
     * 0:不播放排队位置,1:播放排队位置
     */
    private Integer notifyPosition;

    /**
     * 频次
     */
    private Integer notifyRate;

    /**
     * 您前面还有$位用户在等待
     */
    private String notifyContent;

    /**
     * 主叫记忆(1:开启,0:不开启)
     */
    private Integer callMemory;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 当前在线坐席
     */
    private List<String> onlineAgents = new ArrayList<>();

    /**
     * 所有技能
     */
    private List<SkillInfo> skills;

    /**
     * 技能组中坐席分配策略
     */
    private GroupAgentStrategyInfo groupAgentStrategyPo;

    /**
     * 技能组溢出策略
     */
    private List<GroupOverFlowInfo> groupOverflows;

    /**
     * 主叫显号号码池
     */
    private List<String> callerDisplays;

    /**
     * 被叫显号号码池
     */
    private List<String> calledDisplays;

    /**
     * 坐席记忆配置
     */
    private GroupMemoryConfigInfo groupMemoryConfig;

    /**
     * 最大空闲时长
     */
    private Integer maxWaitTime = 0;

    /**
     * 最大空闲时长
     */
    private Integer maxReadyTime = 0;

    /**
     * 呼入总数
     */
    private Integer callInTotal = 0;

    /**
     * 呼入应答数
     */
    private Integer callInAnswer = 0;

    /**
     * 呼入最后的分配时间
     */
    private Date lastServiceTime;
}
