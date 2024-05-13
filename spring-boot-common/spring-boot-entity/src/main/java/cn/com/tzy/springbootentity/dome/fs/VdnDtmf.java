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
    * 路由按键导航表
    */
@ApiModel(description="路由按键导航表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_vdn_dtmf")
public class VdnDtmf extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 按键导航ID
     */
    @TableField(value = "navigate_id")
    @ApiModelProperty(value="按键导航ID")
    private Long navigateId;

    /**
     * 按键
     */
    @TableField(value = "dtmf")
    @ApiModelProperty(value="按键")
    private String dtmf;

    /**
     * 路由类型(1:技能组,2:IVR,3:路由字码,4:坐席分机,5:挂机)
     */
    @TableField(value = "route_type")
    @ApiModelProperty(value="路由类型(1:技能组,2:IVR,3:路由字码,4:坐席分机,5:挂机)")
    private Integer routeType;

    /**
     * 路由值
     */
    @TableField(value = "route_value")
    @ApiModelProperty(value="路由值")
    private Long routeValue;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}