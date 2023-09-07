package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(value = "用户基本表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_user")
public class User extends LongIdEntity {
    /**
     * 人员名称
     */
    @TableField(value = "user_name")
    @ApiModelProperty(value = "人员名称")
    private String userName;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 账号
     */
    @TableField(value = "login_account")
    @ApiModelProperty(value = "账号")
    private String loginAccount;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 加盐
     */
    @TableField(value = "credentialssalt")
    @ApiModelProperty(value = "加盐")
    private String credentialssalt;

    /**
     * 图像地址
     */
    @TableField(value = "image_url")
    @ApiModelProperty(value = "图像地址")
    private String imageUrl;

    /**
     * 电话
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 性别 0.未知 1.男 2.女
     */
    @TableField(value = "gender")
    @ApiModelProperty(value = "性别 0.未知 1.男 2.女")
    private Integer gender;

    /**
     * 身份证号
     */
    @TableField(value = "id_card")
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    /**
     * 省区编码
     */
    @TableField(value = "province_id")
    @ApiModelProperty(value = "省区编码")
    private Integer provinceId;

    /**
     * 市区编码
     */
    @TableField(value = "city_id")
    @ApiModelProperty(value = "市区编码")
    private Integer cityId;

    /**
     * 县区编码
     */
    @TableField(value = "area_id")
    @ApiModelProperty(value = "县区编码")
    private Integer areaId;

    /**
     * 居住地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value = "居住地址")
    private String address;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;

    /**
     * 登录时间
     */
    @TableField(value = "login_last_time")
    @ApiModelProperty(value = "登录时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date loginLastTime;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}