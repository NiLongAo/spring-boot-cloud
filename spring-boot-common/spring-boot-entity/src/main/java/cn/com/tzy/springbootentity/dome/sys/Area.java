package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "地区码表")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_area")
public class Area extends Base {
    /**
     * 地区Id
     */
    @TableId(value = "area_id", type = IdType.AUTO)
    @ApiModelProperty(value = "地区Id")
    private Integer areaId;

    /**
     * 地区父节点
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value = "地区父节点")
    private Integer parentId;

    /**
     * 地区编码
     */
    @TableField(value = "area_code")
    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    /**
     * 地区名
     */
    @TableField(value = "area_name")
    @ApiModelProperty(value = "地区名")
    private String areaName;

    /**
     * 地区级别（1:省份province,2:市city,3:区县district,4:街道street）
     */
    @TableField(value = "level")
    @ApiModelProperty(value = "地区级别（1:省份province,2:市city,3:区县district,4:街道street）")
    private Integer level;

    /**
     * 城市编码
     */
    @TableField(value = "city_code")
    @ApiModelProperty(value = "城市编码")
    private String cityCode;

    /**
     * 城市中心点（即：经纬度坐标）
     */
    @TableField(value = "center")
    @ApiModelProperty(value = "城市中心点（即：经纬度坐标）")
    private String center;
}
