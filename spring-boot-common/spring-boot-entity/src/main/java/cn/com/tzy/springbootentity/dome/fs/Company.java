package cn.com.tzy.springbootentity.dome.fs;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 企业信息表
    */
@ApiModel(description="企业信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_company")
public class Company extends LongIdEntity {
    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 父企业ID
     */
    @TableField(value = "id_path")
    @ApiModelProperty(value="父企业ID")
    private String idPath;

    /**
     * 父企业
     */
    @TableField(value = "pid")
    @ApiModelProperty(value="父企业")
    private Long pid;

    /**
     * 简称
     */
    @TableField(value = "company_code")
    @ApiModelProperty(value="简称")
    private String companyCode;

    /**
     * 时区
     */
    @TableField(value = "gmt")
    @ApiModelProperty(value="时区")
    private Integer gmt;

    /**
     * 联系人
     */
    @TableField(value = "contact")
    @ApiModelProperty(value="联系人")
    private String contact;

    /**
     * 电话
     */
    @TableField(value = "phone")
    @ApiModelProperty(value="电话")
    private String phone;

    /**
     * 金额
     */
    @TableField(value = "balance")
    @ApiModelProperty(value="金额")
    private Long balance;

    /**
     * 1:呼出计费,2:呼入计费,3:双向计费,0:全免费
     */
    @TableField(value = "bill_type")
    @ApiModelProperty(value="1:呼出计费,2:呼入计费,3:双向计费,0:全免费")
    private Integer billType;

    /**
     * 0:预付费;1:后付费
     */
    @TableField(value = "pay_type")
    @ApiModelProperty(value="0:预付费;1:后付费")
    private Integer payType;

    /**
     * 隐藏客户号码(0:不隐藏;1:隐藏)
     */
    @TableField(value = "hidden_customer")
    @ApiModelProperty(value="隐藏客户号码(0:不隐藏;1:隐藏)")
    private Integer hiddenCustomer;

    /**
     * 坐席密码等级
     */
    @TableField(value = "secret_type")
    @ApiModelProperty(value="坐席密码等级")
    private Integer secretType;

    /**
     * 验证秘钥
     */
    @TableField(value = "secret_key")
    @ApiModelProperty(value="验证秘钥")
    private String secretKey;

    /**
     * IVR通道数
     */
    @TableField(value = "ivr_limit")
    @ApiModelProperty(value="IVR通道数")
    private Integer ivrLimit;

    /**
     * 开通坐席
     */
    @TableField(value = "agent_limit")
    @ApiModelProperty(value="开通坐席")
    private Integer agentLimit;

    /**
     * 开通技能组
     */
    @TableField(value = "group_limit")
    @ApiModelProperty(value="开通技能组")
    private Integer groupLimit;

    /**
     * 单技能组中坐席上限
     */
    @TableField(value = "group_agent_limit")
    @ApiModelProperty(value="单技能组中坐席上限")
    private Integer groupAgentLimit;

    /**
     * 录音保留天数
     */
    @TableField(value = "record_storage")
    @ApiModelProperty(value="录音保留天数")
    private Integer recordStorage;

    /**
     * 话单回调通知
     */
    @TableField(value = "notify_url")
    @ApiModelProperty(value="话单回调通知")
    private String notifyUrl;

    /**
     * 状态(0:禁用企业,1:免费企业;2:试用企业,3:付费企业)
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态(0:禁用企业,1:免费企业;2:试用企业,3:付费企业)")
    private Integer status;
}