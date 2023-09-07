package cn.com.tzy.springbootentity.param.sys;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@ApiModel("客户端信息")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientParam extends PageModel {

    @ApiModelProperty("客户端ID")
    public String clientId;

    @ApiModelProperty("客户端密钥")
    public String resourceIds;

    @ApiModelProperty("资源id列表")
    public String clientSecret;

    @ApiModelProperty("域")
    public String scope;

    @ApiModelProperty("授权方式")
    public String authorizedGrantTypes;

    @ApiModelProperty("回调地址")
    public String webServerRedirectUri;

    @ApiModelProperty("权限列表")
    public String authorities;

    @ApiModelProperty("认证令牌时效")
    public Integer accessTokenValidity;

    @ApiModelProperty("刷新令牌时效")
    public Integer refreshTokenValidity;

    @ApiModelProperty("扩展信息")
    public String additionalInformation;

    @ApiModelProperty("是否自动放行")
    public String autoapprove;
}
