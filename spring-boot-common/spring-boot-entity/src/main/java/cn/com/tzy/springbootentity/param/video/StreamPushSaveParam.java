package cn.com.tzy.springbootentity.param.video;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("推流请求类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamPushSaveParam {

    /**
     * 应用名
     */
    @ApiModelProperty(value="应用名")
    private String app;

    /**
     * 流id
     */
    @ApiModelProperty(value="流id")
    private String stream;


    /**
     * 国标ID
     */
    @TableField(value = "gb_id")
    @ApiModelProperty(value="国标ID")
    private String gbId;

    /**
     * 名称
     */
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 经度
     */
    @ApiModelProperty(value="经度")
    private Double longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value="纬度")
    private Double latitude;
    
}
