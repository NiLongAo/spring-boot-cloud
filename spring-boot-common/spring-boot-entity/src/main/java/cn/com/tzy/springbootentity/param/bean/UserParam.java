package cn.com.tzy.springbootentity.param.bean;


import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户基本表
 */
@ApiModel("用户信息")
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserParam extends PageModel {

    @NotNull(message = "id不能为空", groups = {edit.class,updateInfo.class})
    @ApiModelProperty("用户编号")
    public Long id;


    @ApiModelProperty("人员名称")
    @NotBlank(message = "人员名称不能为空", groups = {add.class,edit.class,updateInfo.class})
    public String userName;


    @ApiModelProperty("昵称")
    @NotBlank(message = "昵称不能为空", groups = {add.class,edit.class,updateInfo.class})
    public String nickName;


    @ApiModelProperty("账号")
    @NotBlank(message = "账号不能为空", groups = {add.class})
    public String loginAccount;


    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空", groups = {add.class})
    public String password;

    @ApiModelProperty("图像地址")
    @NotBlank(message = "图像地址不能为空", groups = {add.class,edit.class,updateInfo.class})
    public String imageUrl;


    @ApiModelProperty("电话")
    public String phone;


    @ApiModelProperty("性别 0.未知 1.男 2.女")
    public Integer gender;

    @ApiModelProperty("身份证号")
    public String idCard;

    @ApiModelProperty("省区编码")
    public Integer provinceId;


    @ApiModelProperty("市区编码")
    public Integer cityId;


    @ApiModelProperty("县区编码")
    public Integer areaId;


    @ApiModelProperty("居住地址")
    public String address;

    @ApiModelProperty("备注")
    public String memo;


    @ApiModelProperty("是否核心管理员 1是 0否")
    @NotNull(message = "是否核心管理员不能为空", groups = {add.class,edit.class})
    public Integer isAdmin;

    @ApiModelProperty("启用状态 1.启用 0.禁用")
    @NotNull(message = "启用状态不能为空", groups = {add.class,edit.class})
    public Integer isEnabled;

    @ApiModelProperty("验证码")
    public String verificationCode;

    @ApiModelProperty("用户类型枚举")
    public ConstEnum.UserTypeEnum type;

    @ApiModelProperty("用户类型编号")
    public Long typeId;

    @ApiModelProperty("搜素条件")
    public String search;

    /**
     * 参数校验分组：编辑
     */
    public @interface updateInfo {
    }
}
