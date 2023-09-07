package cn.com.tzy.springbootentity.vo.video;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChannelTreeVo {

    /**
     * 父级id
     */
    @ApiModelProperty(value="父级id")
    private String parentId;

    /**
     * 类型 1.设备 2.通道
     */
    @ApiModelProperty(value="父级id")
    private Integer type;

    /**
     * 编号
     */
    @ApiModelProperty(value="父级id")
    private String id;

    /**
     * 设备通道
     */
    @TableField(value = "device_id")
    private String deviceId;

    /**
     * 名称
     */
    @ApiModelProperty(value="父级id")
    private String name;

    /**
     * 状态 0.离线 1.在线
     */
    @ApiModelProperty(value="在线状态")
    private Integer status;

}
