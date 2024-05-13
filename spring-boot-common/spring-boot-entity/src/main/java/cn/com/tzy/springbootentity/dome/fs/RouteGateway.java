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
    * 媒体网关表
    */
@ApiModel(description="媒体网关表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_route_gateway")
public class RouteGateway extends LongIdEntity {
    /**
     * 号码
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="号码")
    private String name;

    /**
     * 媒体地址
     */
    @TableField(value = "media_host")
    @ApiModelProperty(value="媒体地址")
    private String mediaHost;

    /**
     * 媒体端口
     */
    @TableField(value = "media_port")
    @ApiModelProperty(value="媒体端口")
    private Integer mediaPort;

    /**
     * 主叫号码前缀
     */
    @TableField(value = "caller_prefix")
    @ApiModelProperty(value="主叫号码前缀")
    private String callerPrefix;

    /**
     * 被叫号码前缀
     */
    @TableField(value = "called_prefix")
    @ApiModelProperty(value="被叫号码前缀")
    private String calledPrefix;

    /**
     * fs的context规则
     */
    @TableField(value = "profile")
    @ApiModelProperty(value="fs的context规则")
    private String profile;

    /**
     * sip头1
     */
    @TableField(value = "sip_header1")
    @ApiModelProperty(value="sip头1")
    private String sipHeader1;

    /**
     * sip头2
     */
    @TableField(value = "sip_header2")
    @ApiModelProperty(value="sip头2")
    private String sipHeader2;

    /**
     * sip头3
     */
    @TableField(value = "sip_header3")
    @ApiModelProperty(value="sip头3")
    private String sipHeader3;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}