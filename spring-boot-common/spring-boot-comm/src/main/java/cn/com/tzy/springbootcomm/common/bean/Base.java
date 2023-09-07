package cn.com.tzy.springbootcomm.common.bean;

import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体对象
 *
 * @author 芋道源码
 */

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class Base implements Serializable {

    /**
     * 创建人编号
     */
    @TableField(value = "create_user_id",fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人编号")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date createTime;

    /**
     * 修改人编号
     */
    @TableField(value = "update_user_id",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改人编号")
    private Long updateUserId;

    /**
     * 修改时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date updateTime;
}
