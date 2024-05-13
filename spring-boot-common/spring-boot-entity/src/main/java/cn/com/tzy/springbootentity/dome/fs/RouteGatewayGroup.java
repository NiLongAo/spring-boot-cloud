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
    * 路由与网关组关系表
    */
@ApiModel(description="路由与网关组关系表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_route_gateway_group")
public class RouteGatewayGroup extends LongIdEntity {
    /**
     * 网关组
     */
    @TableField(value = "route_group_id")
    @ApiModelProperty(value="网关组")
    private Long routeGroupId;

    /**
     * 媒体网关
     */
    @TableField(value = "gateway_id")
    @ApiModelProperty(value="媒体网关")
    private Long gatewayId;
}