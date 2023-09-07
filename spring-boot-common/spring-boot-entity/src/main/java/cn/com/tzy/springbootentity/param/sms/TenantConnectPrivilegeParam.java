package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value="租户联权限表")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantConnectPrivilegeParam extends BaseModel {
    /**
     * 租户编号
     */
    @ApiModelProperty(value="租户编号")
    @NotNull(message = "租户编号不能为空",groups = {add.class})
    public Long tenantId;

    /**
     * 权限编号
     */
    @ApiModelProperty(value="权限编号")
    public List<String> privilegeList;
}