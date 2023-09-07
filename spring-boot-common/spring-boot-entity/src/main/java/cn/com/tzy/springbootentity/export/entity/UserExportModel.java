package cn.com.tzy.springbootentity.export.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.com.tzy.springbootcomm.annotation.Desensitized;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.enumcom.SensitiveTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 用户可导出字段实体类
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserExportModel {

    @Excel(name = "人员名称",orderNum = "0",width = 15)
    @Desensitized(type = SensitiveTypeEnum.CHINESE_NAME)
    private String userName;

    @Excel(name = "昵称",orderNum = "1",width = 15)
    private String nickName;

    @Excel(name = "账号",orderNum = "2",width = 15)
    private String loginAccount;

    @Excel(name = "密码",orderNum = "3",width = 15)
    @Desensitized(type = SensitiveTypeEnum.PASSWORD)
    private String password;

    @Excel(name = "加盐",orderNum = "4",width = 15)
    private String credentialssalt;

    @Excel(name = "图像地址",orderNum = "5",width = 15)
    private String imageUrl;

    @Excel(name = "电话",orderNum = "6",width = 15)
    @Desensitized(type = SensitiveTypeEnum.MOBILE_PHONE)
    private String phone;

    @Excel(name = "性别",dict = "GENDER",orderNum = "7",width = 15)
    private Integer gender;

    @Excel(name = "身份证号",orderNum = "8",width = 20)
    @Desensitized(type = SensitiveTypeEnum.ID_CARD)
    private String idCard;


    @Excel(name = "省区编码",orderNum = "9",width = 15)
    private Integer provinceId;

    @Excel(name = "市区编码",orderNum = "10",width = 15)
    private Integer cityId;

    @Excel(name = "县区编码",orderNum = "11",width = 15)
    private Integer areaId;

    @Excel(name = "居住地址",orderNum = "12",width = 15)
    @Desensitized(type = SensitiveTypeEnum.ADDRESS)
    private String address;

    @Excel(name = "备注",orderNum = "13",width = 15)
    private String memo;

    @Excel(name = "登录时间",format = Constant.DATE_TIME_FORMAT,orderNum = "14",width = 20)
    private Date loginLastTime;

    @Excel(name = "修改人编号",orderNum = "15",width = 15)
    private Long updateUserId;


    @Excel(name = "修改时间",format = Constant.DATE_TIME_FORMAT,orderNum = "16",width = 20)
    private Date updateTime;

    /**
     * 创建人编号
     */
    @Excel(name = "修改人编号",orderNum = "17",width = 15)
    private Long createUserId;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间",format = Constant.DATE_TIME_FORMAT,orderNum = "18",width = 20)
    private Date createTime;
}
