package cn.com.tzy.springbootentity.param.sys;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("租户信息")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TenantParam extends PageModel {

    /**
     * 编号
     */
    @NotNull(message = "未获取到编号",groups = {edit.class})
    private Long id;
    /**
     * 租户名
     */
    @NotBlank(message = "未获取到租户名",groups = {add.class,edit.class})
    private String tenantName;

    /**
     * 租户联系人编号
     */
    private Long tenantUserId;

    /**
     * 租户联系人名称
     */
    private String tenantUserName;

    /**
     * 租户状态（0正常 1停用）
     */
    @NotNull(message = "未获取租户状态",groups = {add.class,edit.class})
    private Integer status;

    /**
     * 账号数量
     */
    @NotNull(message = "未获取到账号数量",groups = {add.class,edit.class})
    private Integer accountCount;

}
