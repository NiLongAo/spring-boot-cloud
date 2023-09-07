package cn.com.tzy.springbootentity.dome.video;

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
    * 国标级联关联通道信息
    */
@ApiModel(value="国标级联关联通道信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_platform_gb_channel")
public class PlatformGbChannel extends LongIdEntity {
    /**
     * 平台ID
     */
    @TableField(value = "platform_id")
    @ApiModelProperty(value="平台ID")
    private String platformId;

    /**
     * 目录ID
     */
    @TableField(value = "catalog_id")
    @ApiModelProperty(value="目录ID")
    private String catalogId;

    /**
     * deviceChannel的数据库国标编号
     */
    @TableField(value = "device_channel_id")
    @ApiModelProperty(value="deviceChannel的数据库自增ID")
    private String deviceChannelId;
}