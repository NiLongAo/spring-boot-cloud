package cn.com.tzy.springbootentity.param.bean;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("系统日志信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogsParam extends PageModel {

    /**
     * 日志类型 0.其他 1.登录 2.新增 3.修改 4.删除
     */
    @ApiModelProperty(value="日志类型 0.其他 1.登录 2.新增 3.修改 4.删除")
    public Integer type;

    /**
     * 访问者ip
     */
    @ApiModelProperty(value="访问者ip")
    public String ip;

    /**
     * 访问者网络地址
     */
    @ApiModelProperty(value="访问者网络地址")
    public String ipAttribution;

    /**
     * 请求方式
     */
    @ApiModelProperty(value="请求方式")
    public String method;

    /**
     * 访问接口
     */
    @ApiModelProperty(value="访问接口")
    public String api;

    /**
     * 请求参数
     */
    @ApiModelProperty(value="请求参数")
    public String param;

    /**
     * 响应参数
     */
    @ApiModelProperty(value="响应参数")
    public String result;

    /**
     * 持续时间
     */
    @ApiModelProperty(value="持续时间")
    public Integer duration;

    /**
     * 持续时间开始
     */
    @ApiModelProperty(value="持续时间")
    public Integer durationStart;

    /**
     * 持续时间结束
     */
    @ApiModelProperty(value="持续时间")
    public Integer durationEnd;
}
