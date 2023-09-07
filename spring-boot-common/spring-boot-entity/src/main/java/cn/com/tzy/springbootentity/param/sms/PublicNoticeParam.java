package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 平台通知公告
 */
@ApiModel(value = "平台通知公告")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PublicNoticeParam extends PageModel {


    @ApiModelProperty(value = "主键")
    @NotNull(message = "编号不能为空",groups ={edit.class,delete.class})
    public Long id;
    /**
     * 通知类型
     */
    @ApiModelProperty(value = "通知类型")
    @NotNull(message = "通知类型不能为空",groups ={add.class,edit.class})
    public Integer noticeType;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @NotEmpty(message = "请输入标题",groups = {add.class,edit.class})
    public String title;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    @NotEmpty(message = "请输入内容",groups = {add.class,edit.class})
    public String content;

    /**
     * 公告开始时间
     */
    @ApiModelProperty(value = "公告开始时间")
    @NotNull(message = "请输入开始时间",groups = {add.class,edit.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date beginTime;

    /**
     * 公告结束时间
     */
    @ApiModelProperty(value = "公告结束时间")
    @NotNull(message = "请输入结束时间",groups = {add.class,edit.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date endTime;

    /**
     * 状态 1正常,2已过期
     */
    @ApiModelProperty(value = "状态 1正常,2已过期")
    public Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createTime;

    /**
     * 用户分页查询字段
     */
    @ApiModelProperty(value = "用户编号")
    public Long userId;

}