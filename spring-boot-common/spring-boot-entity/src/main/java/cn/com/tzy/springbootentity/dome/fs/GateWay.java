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
    * 网关中继信息
    */
@ApiModel(description="网关中继信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_gate_way")
public class GateWay extends LongIdEntity {
    /**
     * 网关名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="网关名称")
    private String name;

    /**
     * 关联路由id
     */
    @TableField(value = "route_id")
    @ApiModelProperty(value="关联路由id")
    private String routeId;

    /**
     * 服务器地址
     */
    @TableField(value = "realm")
    @ApiModelProperty(value="服务器地址")
    private String realm;

    /**
     * 是否注册
     */
    @TableField(value = "register")
    @ApiModelProperty(value="是否注册")
    private Integer register;

    /**
     * 传输类型
     */
    @TableField(value = "transport")
    @ApiModelProperty(value="传输类型")
    private Integer transport;

    /**
     * 重连间隔（秒）
     */
    @TableField(value = "retry_seconds")
    @ApiModelProperty(value="重连间隔（秒）")
    private Integer retrySeconds;

    /**
     * 账户
     */
    @TableField(value = "username")
    @ApiModelProperty(value="账户")
    private Integer username;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value="密码")
    private String password;

    /**
     * 是否选中0:未选中，1：已选中
     */
    @TableField(value = "selected")
    @ApiModelProperty(value="是否选中0:未选中，1：已选中")
    private String selected;
}