package cn.com.tzy.springbootentity.dome.face;

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
import lombok.NoArgsConstructor;

/**
 * 人员信息
 */
@ApiModel(value = "人员信息")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "face_person")
public class Person extends LongIdEntity {
    /**
     * 图片自定义编号
     */
    @TableField(value = "img_id")
    @ApiModelProperty(value = "图片自定义编号")
    private String imgId;

    /**
     * 图片地址
     */
    @TableField(value = "img_url")
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    /**
     * 人脸特征数组
     */
    @TableField(value = "`extract`")
    @ApiModelProperty(value = "人脸特征数组")
    private String extract;

    /**
     * 人员姓名
     */
    @TableField(value = "person_name")
    @ApiModelProperty(value = "人员姓名")
    private String personName;

    /**
     * 年龄
     */
    @TableField(value = "person_age")
    @ApiModelProperty(value = "年龄")
    private Integer personAge;

    /**
     * 性别 0.未知 1.男 2.女
     */
    @TableField(value = "gender")
    @ApiModelProperty(value = "性别 0.未知 1.男 2.女")
    private Integer gender;

    /**
     * 地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value = "地址")
    private String address;
}