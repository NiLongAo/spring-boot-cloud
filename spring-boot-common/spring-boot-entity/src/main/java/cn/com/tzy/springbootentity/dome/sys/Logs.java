package cn.com.tzy.springbootentity.dome.sys;

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
    * 系统日志
    */
@ApiModel(value="系统日志")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_logs")
public class Logs extends LongIdEntity {
    /**
     * 日志类型 0.其他 1.登录 2.新增 3.修改 4.删除
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="日志类型 0.其他 1.登录 2.新增 3.修改 4.删除")
    private Integer type;

    /**
     * 访问者ip
     */
    @TableField(value = "ip")
    @ApiModelProperty(value="访问者ip")
    private String ip;

    /**
     * ip归属地信息
     */
    @TableField("ip_attribution")
    private String ipAttribution;

    /**
     * 请求方式
     */
    @TableField(value = "method")
    @ApiModelProperty(value="请求方式")
    private String method;

    /**
     * 访问接口
     */
    @TableField(value = "api")
    @ApiModelProperty(value="访问接口")
    private String api;

    /**
     * 请求参数
     */
    @TableField(value = "param")
    @ApiModelProperty(value="请求参数")
    private String param;

    /**
     * 响应参数
     */
    @TableField(value = "`result`")
    @ApiModelProperty(value="响应参数")
    private String result;

    /**
     * 持续时间
     */
    @TableField(value = "duration")
    @ApiModelProperty(value="持续时间")
    private Integer duration;

}