package cn.com.tzy.springbootentity.vo.video;

import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StreamProxyVo extends StreamProxy {

    /**
     * 国标流编号
     */
    @ApiModelProperty(value="国标流编号")
    private Long gbStreamId;

    /**
     * 国标ID
     */
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
