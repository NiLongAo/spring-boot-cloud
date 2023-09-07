package cn.com.tzy.springbootentity.vo.bean;

import cn.com.tzy.springbootcomm.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserInfoVo implements Serializable {

    /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户图像
     */
    private String imageUrl;

    /**
     * 登录账号
     */
    private String loginAccount;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 省编码
     */
    private Integer provinceId;

    /**
     * 市编码
     */
    private Integer cityId;

    /**
     * 区编码
     */
    private Integer areaId;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 住址
     */
    private String address;

    /**
     * 备注
     */
    private String memo;

    /**
     * 最后登录时间
     */
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date loginLastTime;
    /**
     * 是否系统管理员
     */
    private Integer isAdmin;

    /**
     * 是否启用
     */
    private Integer isEnabled;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 租户是否被禁用
     */
    private Integer tenantStatus;
    /**
     * 是否绑定微信小程序
     */
    private Integer wxMiniStatus;
    /**
     * 用户角色
     */
    private  List<Long> roleIdList;
    /**
     * 用户职位
     */
    private  List<Long> positionIdList;
    /**
     * 用户部门
     */
    private  List<Long> departmentIdList;
    /**
     * 用户权限
     */
    private List<String> privilegeList;

}
