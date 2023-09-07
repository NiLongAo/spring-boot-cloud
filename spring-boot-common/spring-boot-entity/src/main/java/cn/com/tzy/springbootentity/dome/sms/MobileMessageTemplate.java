package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.com.tzy.springbootcomm.common.bean.IntIdEntity;
import cn.com.tzy.springbootstartersmsbasic.demo.MessageTemplate;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "短信模板")
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_mobile_message_template")
public class MobileMessageTemplate extends Base implements MessageTemplate {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "config_id")
    @ApiModelProperty(value = "短信接口id")
    private Integer configId;


    @TableField(value = "code")
    @ApiModelProperty(value = "编号")
    private String code;


    @TableField(value = "type")
    @ApiModelProperty(value = "类型")
    private Integer type;


    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;


    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;


    @TableField(value = "receiver")
    @ApiModelProperty(value = "接收人")
    private String receiver;

    @TableField(value = "variable")
    @ApiModelProperty(value = "变量")
    private String variable;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setVariable(String variable) {
        this.variable = variable;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}