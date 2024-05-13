package cn.com.tzy.springbootentity.dome.fs;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * ivr流程表
    */
@ApiModel(description="ivr流程表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_ivr_workflow")
public class IvrWorkflow extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 流程名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="流程名称")
    private String name;

    /**
     * 流程文件名
     */
    @TableField(value = "oss_id")
    @ApiModelProperty(value="流程文件名")
    private String ossId;

    /**
     * 用来存贮 ivr 流程启动所需要的参数描述
     */
    @TableField(value = "init_params")
    @ApiModelProperty(value="用来存贮 ivr 流程启动所需要的参数描述")
    private String initParams;

    /**
     * 流程发布人
     */
    @TableField(value = "create_user")
    @ApiModelProperty(value="流程发布人")
    private String createUser;

    /**
     * 流程审核人
     */
    @TableField(value = "verify_user")
    @ApiModelProperty(value="流程审核人")
    private String verifyUser;

    /**
     * 流程内容(ivr)
     */
    @TableField(value = "content")
    @ApiModelProperty(value="流程内容(ivr)")
    private String content;

    /**
     * 该流程用到的语音文件id，以英文逗号,分隔
     */
    @TableField(value = "voice_item")
    @ApiModelProperty(value="该流程用到的语音文件id，以英文逗号,分隔")
    private String voiceItem;

    /**
     * 1转接，2咨询
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="1转接，2咨询")
    private Integer type;

    /**
     * 流程状态    1：待发布   2：审核中  3：审核未通过  4：审核通过  5：已上线(ivr)
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="流程状态    1：待发布   2：审核中  3：审核未通过  4：审核通过  5：已上线(ivr)")
    private Integer status;
}