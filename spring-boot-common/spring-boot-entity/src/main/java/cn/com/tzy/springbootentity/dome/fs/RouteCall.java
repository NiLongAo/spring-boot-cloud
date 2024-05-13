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
    * 字冠路由表
    */
@ApiModel(description="字冠路由表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_route_call")
public class RouteCall extends LongIdEntity {
    /**
     * 所属企业
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="所属企业")
    private Long companyId;

    /**
     * 所属路由组
     */
    @TableField(value = "route_group_id")
    @ApiModelProperty(value="所属路由组")
    private Long routeGroupId;

    /**
     * 字冠号码
     */
    @TableField(value = "route_num")
    @ApiModelProperty(value="字冠号码")
    private String routeNum;

    /**
     * 最长
     */
    @TableField(value = "num_max")
    @ApiModelProperty(value="最长")
    private Integer numMax;

    /**
     * 最短
     */
    @TableField(value = "num_min")
    @ApiModelProperty(value="最短")
    private Integer numMin;

    /**
     * 主叫替换规则
     */
    @TableField(value = "caller_change")
    @ApiModelProperty(value="主叫替换规则")
    private Integer callerChange;

    /**
     * 替换号码
     */
    @TableField(value = "caller_change_num")
    @ApiModelProperty(value="替换号码")
    private String callerChangeNum;

    /**
     * 被叫替换规则
     */
    @TableField(value = "called_change")
    @ApiModelProperty(value="被叫替换规则")
    private Integer calledChange;

    /**
     * 替换号码
     */
    @TableField(value = "called_change_num")
    @ApiModelProperty(value="替换号码")
    private String calledChangeNum;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}