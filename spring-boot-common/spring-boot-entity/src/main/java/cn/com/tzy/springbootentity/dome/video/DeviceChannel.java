package cn.com.tzy.springbootentity.dome.video;

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

/**
    * 通道信息
    */
@ApiModel(value="通道信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_device_channel")
public class DeviceChannel extends LongIdEntity {
    /**
     * 父级id
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="父级id")
    private String parentId;
    /**
     * 通道国标编号
     * 国标编码规范
     * 1.2位 省级编码
     * 3.4位 市级编码
     * 5.6位 区级编码
     * 7.8 基层接入单位编号
     * 9.10 行业编码
     * 11.12.13 类型编码
     * 14 网络标识编码
     * 15~20 设备 用户序号
     */
    @TableField(value = "channel_id")
    @ApiModelProperty(value="通道国标编号")
    private String channelId;

    /**
     * 设备国标编号
     */
    @TableField(value = "device_id")
    @ApiModelProperty(value="设备国标编号")
    private String deviceId;

    /**
     * 通道名
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="通道名")
    private String name;

    /**
     * 生产厂商
     */
    @TableField(value = "manufacture")
    @ApiModelProperty(value="生产厂商")
    private String manufacture;

    /**
     * 型号
     */
    @TableField(value = "model")
    @ApiModelProperty(value="型号")
    private String model;

    /**
     * 设备归属
     */
    @TableField(value = "`owner`")
    @ApiModelProperty(value="设备归属")
    private String owner;

    /**
     * 行政区域
     */
    @TableField(value = "civil_code")
    @ApiModelProperty(value="行政区域")
    private String civilCode;

    /**
     * 警区
     */
    @TableField(value = "block")
    @ApiModelProperty(value="警区")
    private String block;

    /**
     * 安装地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value="安装地址")
    private String address;

    /**
     * 是否有子设备 1有, 0没有
     */
    @TableField(value = "parental")
    @ApiModelProperty(value="是否有子设备 1有, 0没有")
    private Integer parental;

    /**
     * 信令安全模式  缺省为0; 0:不采用; 2: S/MIME签名方式; 3: S/ MIME加密签名同时采用方式; 4:数字摘要方式
     */
    @TableField(value = "safety_way")
    @ApiModelProperty(value="信令安全模式  缺省为0; 0:不采用; 2: S/MIME签名方式; 3: S/ MIME加密签名同时采用方式; 4:数字摘要方式")
    private Integer safetyWay;

    /**
     * 注册方式 缺省为1;1:符合IETFRFC3261标准的认证注册模 式; 2:基于口令的双向认证注册模式; 3:基于数字证书的双向认证注册模式
     */
    @TableField(value = "register_way")
    @ApiModelProperty(value="注册方式 缺省为1;1:符合IETFRFC3261标准的认证注册模 式; 2:基于口令的双向认证注册模式; 3:基于数字证书的双向认证注册模式")
    private Integer registerWay;

    /**
     * 证书序列号
     */
    @TableField(value = "cert_num")
    @ApiModelProperty(value="证书序列号")
    private String certNum;

    /**
     * 证书有效标识 缺省为0;证书有效标识:0:无效1: 有效
     */
    @TableField(value = "certifiable")
    @ApiModelProperty(value="证书有效标识 缺省为0;证书有效标识:0:无效1: 有效")
    private Integer certifiable;

    /**
     * 证书无效原因码
     */
    @TableField(value = "err_code")
    @ApiModelProperty(value="证书无效原因码")
    private Integer errCode;

    /**
     * 证书终止有效期
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="证书终止有效期")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date endTime;

    /**
     * 保密属性 缺省为0; 0:不涉密, 1:涉密
     */
    @TableField(value = "secrecy")
    @ApiModelProperty(value="保密属性 缺省为0; 0:不涉密, 1:涉密")
    private Integer secrecy;

    /**
     * IP地址
     */
    @TableField(value = "ip_address")
    @ApiModelProperty(value="IP地址")
    private String ipAddress;

    /**
     * 端口号
     */
    @TableField(value = "port")
    @ApiModelProperty(value="端口号")
    private Integer port;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value="密码")
    private String password;

    /**
     * 云台类型 0.未知 1.球机 2.半球 3.固定枪机 4.遥控枪机
     */
    @TableField(value = "ptz_type")
    @ApiModelProperty(value="云台类型 0.未知 1.球机 2.半球 3.固定枪机 4.遥控枪机")
    private Integer ptzType;

    /**
     * 云台类型描述字符串
     */
    @TableField(value = "ptz_type_text")
    @ApiModelProperty(value="云台类型描述字符串")
    private String ptzTypeText;

    /**
     * 在线/离线， 1在线,0离线
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="在线/离线， 1在线,0离线")
    private Integer status;

    /**
     * 纬度
     */
    @TableField(value = "longitude")
    @ApiModelProperty(value="纬度")
    private Double longitude;

    /**
     * 经度
     */
    @TableField(value = "latitude")
    @ApiModelProperty(value="经度")
    private Double latitude;

    /**
     * 经度 GCJ02
     */
    @TableField(value = "longitude_gcj02")
    @ApiModelProperty(value="经度 GCJ02")
    private Double longitudeGcj02;

    /**
     * 纬度 GCJ02
     */
    @TableField(value = "latitude_gcj02")
    @ApiModelProperty(value="纬度 GCJ02")
    private Double latitudeGcj02;

    /**
     * 经度 WGS84
     */
    @TableField(value = "longitude_wgs84")
    @ApiModelProperty(value="经度 WGS84")
    private Double longitudeWgs84;

    /**
     * 纬度 WGS84
     */
    @TableField(value = "latitude_wgs84")
    @ApiModelProperty(value="纬度 WGS84")
    private Double latitudeWgs84;

    /**
     * 子设备数
     */
    @TableField(value = "sub_count")
    @ApiModelProperty(value="子设备数")
    private Integer subCount;

    /**
     * 流唯一编号，存在表示正在直播
     */
    @TableField(value = "stream_id")
    @ApiModelProperty(value="流唯一编号，存在表示正在直播")
    private String streamId;

    /**
     * 是否含有音频
     */
    @TableField(value = "has_audio")
    @ApiModelProperty(value="是否含有音频")
    private Integer hasAudio;

    /**
     * 标记通道的类型，0->国标通道 1->直播流通道 2->业务分组/虚拟组织/行政区划
     */
    @TableField(value = "channel_type")
    @ApiModelProperty(value="标记通道的类型，0->国标通道 1->直播流通道 2->业务分组/虚拟组织/行政区划")
    private Integer channelType;

    /**
     * 业务分组
     */
    @TableField(value = "business_group_id")
    @ApiModelProperty(value="业务分组")
    private String businessGroupId;

    /**
     * GPS的更新时间
     */
    @TableField(value = "gps_time")
    @ApiModelProperty(value="GPS的更新时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date gpsTime;

    /**
     * 平台Id
     */
    @TableField(exist = false)
    @ApiModelProperty(value="平台Id")
    private String platformId;

    /**
     * 目录Id
     */
    @TableField(exist = false)
    @ApiModelProperty(value="目录Id")
    private String catalogId;

    /**
     * 生产厂商
     */
    @TableField(exist = false)
    @ApiModelProperty(value="生产厂商")
    private String manufacturer;

    /**
     * wan地址
     */
    @TableField(exist = false)
    @ApiModelProperty(value="wan地址")
    private String hostAddress;
}