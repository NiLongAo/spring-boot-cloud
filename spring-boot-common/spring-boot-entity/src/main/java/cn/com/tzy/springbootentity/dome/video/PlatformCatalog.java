package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
    * 国标级联-目录
    */
@ApiModel(value="国标级联-目录")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_platform_catalog")
public class PlatformCatalog extends StringIdEntity {
    /**
     * 父级目录ID
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="父级目录ID")
    private String parentId;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 平台国标编号
     */
    @TableField(value = "platform_id")
    @ApiModelProperty(value="平台国标编号")
    private String platformId;

    /**
     * 行政区划
     */
    @TableField(value = "civil_code")
    @ApiModelProperty(value="行政区划")
    private String civilCode;

    /**
     * 目录分组
     */
    @TableField(value = "business_group_id")
    @ApiModelProperty(value="目录分组")
    private String businessGroupId;

    /**
     * 子节点数
     */
    @TableField(exist = false)
    private int childrenCount;

    /**
     * 类型：0 目录, 1 国标通道, 2 直播流
     */
    @TableField(exist = false)
    private int type;
}