package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "客户端信息")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_oauth_client")
public class OauthClient extends Base {
    /**
     * 客户端ID
     */
    @TableId(value = "client_id", type = IdType.INPUT)
    @ApiModelProperty(value = "客户端ID")
    private String clientId;

    /**
     * 资源id列表
     */
    @TableField(value = "resource_ids")
    @ApiModelProperty(value = "资源id列表")
    private String resourceIds;

    /**
     * 客户端密钥
     */
    @TableField(value = "client_secret")
    @ApiModelProperty(value = "客户端密钥")
    private String clientSecret;

    /**
     * 域
     */
    @TableField(value = "scope")
    @ApiModelProperty(value = "域")
    private String scope;

    /**
     * 授权方式
     */
    @TableField(value = "authorized_grant_types")
    @ApiModelProperty(value = "授权方式")
    private String authorizedGrantTypes;

    /**
     * 回调地址
     */
    @TableField(value = "web_server_redirect_uri")
    @ApiModelProperty(value = "回调地址")
    private String webServerRedirectUri;

    /**
     * 权限列表
     */
    @TableField(value = "authorities")
    @ApiModelProperty(value = "权限列表")
    private String authorities;

    /**
     * 认证令牌时效
     */
    @TableField(value = "access_token_validity")
    @ApiModelProperty(value = "认证令牌时效")
    private Integer accessTokenValidity;

    /**
     * 刷新令牌时效
     */
    @TableField(value = "refresh_token_validity")
    @ApiModelProperty(value = "刷新令牌时效")
    private Integer refreshTokenValidity;

    /**
     * 扩展信息
     */
    @TableField(value = "additional_information")
    @ApiModelProperty(value = "扩展信息")
    private String additionalInformation;

    /**
     * 是否自动放行
     */
    @TableField(value = "autoapprove")
    @ApiModelProperty(value = "是否自动放行")
    private String autoapprove;



    public enum AuthorizedGrantTypesEnum {
        AUTHORIZATION_CODE("authorization_code", "授权码模式"),
        IMPLICIT("implicit", "简化模式"),
        PASSWORD("password", "密码模式"),
        CLIENT_CREDENTIALS("client_credentials", "客户端模式"),
        REFRESH_TOKEN("refresh_token", "刷新令牌"),
        CODE("code", "普通认证模式"),
        SMS("sms", "短信认证模式"),
        WX_MINI("wx_mini", "微信小程序认证类型"),
        WX_MINI_WEB("wx_mini_web", "微信小程序web认证类型"),
        ;

        private final String value;
        private final String name;

        AuthorizedGrantTypesEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static Map<String, String> map = new HashMap<String, String>();

        static {
            for (AuthorizedGrantTypesEnum s : AuthorizedGrantTypesEnum.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }
}
