package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
    * 微信小程序信息
    */
@ApiModel(value="微信小程序信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_mini")
public class Mini extends LongIdEntity {
    /**
     * 微信登录token
     */
    @TableField(value = "open_id")
    @ApiModelProperty(value="微信登录openId")
    private String openId;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    @ApiModelProperty(value="手机号")
    private String phone;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value="昵称")
    private String nickName;

    /**
     * 微信头像
     */
    @TableField(value = "avatar_url")
    @ApiModelProperty(value="微信头像")
    private String avatarUrl;

    /**
     * 性别 0.未知 1.男 2.女
     */
    @TableField(value = "gender")
    @ApiModelProperty(value="性别 0.未知 1.男 2.女")
    private Integer gender;

    /**
     * 登录时间
     */
    @TableField(value = "login_last_time")
    @ApiModelProperty(value="登录时间")
    private Date loginLastTime;
}