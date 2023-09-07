package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
    * 用户微信小程序中间表
    */
@ApiModel(value="用户微信小程序中间表")
@Data
@EqualsAndHashCode(callSuper=true)
@Builder
@AllArgsConstructor
@TableName(value = "bean_mini_user")
public class MiniUser extends LongIdEntity {
    /**
     * 微信小程序主键
     */
    @TableField(value = "mini_id")
    @ApiModelProperty(value="微信小程序主键")
    private Long miniId;

    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户主键")
    private Long userId;
}