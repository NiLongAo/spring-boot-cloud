package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Getter
@ApiModel(value = "短信接口")
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_sms_config")
public class SmsConfig implements SmsModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "sms_type")
    @ApiModelProperty(value = "类型")
    private Integer smsType;

    /**
     * 配置名称
     */
    @TableField(value = "config_name")
    @ApiModelProperty(value = "配置名称")
    private String configName;

    /**
     * 账号
     */
    @TableField(value = "account")
    @ApiModelProperty(value = "账号")
    private String account;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 余额
     */
    @TableField(value = "balance")
    @ApiModelProperty(value = "余额")
    private String balance;

    /**
     * 是否启用
     */
    @TableField(value = "is_active")
    @ApiModelProperty(value = "是否启用")
    private Integer isActive;

    /**
     * 签名
     */
    @TableField(value = "sign")
    @ApiModelProperty(value = "签名")
    private String sign;

    /**
     * 签名位置
     */
    @TableField(value = "sign_place")
    @ApiModelProperty(value = "签名位置")
    private Integer signPlace;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;

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

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }

    @Override
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    @Override
    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public void setSignPlace(Integer signPlace) {
        this.signPlace = signPlace;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public enum Type {
        DXW(1, "短信网"),
        CLW(2, "创蓝网"),
        WND(3, "维纳多"),
        SWLH(4, "商务领航"),
        ALYDY(5, "阿里云大于"),
        WYYD(6, "网易易盾"),
        YTX(7, "云通讯"),
        TXY(8, "腾讯云"),
        ;

        private final int value;
        private final String name;

        private Type(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (Type e : Type.values()) {
                map.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return map.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum SignPlace {
        LEFT(1, "左边"),
        RIGHT(2, "右边"),
        ;

        private final int value;
        private final String name;

        private SignPlace(int value, String name) {
            this.value = value;
            this.name = name;
        }

        private static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (SignPlace e : SignPlace.values()) {
                map.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return map.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}