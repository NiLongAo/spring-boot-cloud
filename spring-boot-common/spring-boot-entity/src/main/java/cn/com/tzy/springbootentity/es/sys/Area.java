package cn.com.tzy.springbootentity.es.sys;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.common.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "地区码表")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@IndexName(value = "bean_sys_area")
public class Area extends Base {
    /**
     * 地区Id
     */
    @ApiModelProperty(value = "地区Id")
    @IndexId(type = IdType.CUSTOMIZE)
    private Integer areaId;

    /**
     * 地区父节点
     */
    @ApiModelProperty(value = "地区父节点")
    private Integer parentId;

    /**
     * 地区编码
     */
    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    /**
     * 地区名
     */
    @ApiModelProperty(value = "地区名")
    private String areaName;

    /**
     * 地区级别（1:省份province,2:市city,3:区县district,4:街道street）
     */
    @ApiModelProperty(value = "地区级别（1:省份province,2:市city,3:区县district,4:街道street）")
    private Integer level;

    /**
     * 城市编码
     */
    @ApiModelProperty(value = "城市编码")
    private String cityCode;

    /**
     * 城市中心点（即：经纬度坐标）
     */
    @ApiModelProperty(value = "城市中心点（即：经纬度坐标）")
    private String center;
}
