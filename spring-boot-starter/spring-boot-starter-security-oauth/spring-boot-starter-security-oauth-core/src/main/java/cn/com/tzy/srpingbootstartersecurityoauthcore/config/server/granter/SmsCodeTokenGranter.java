package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.granter;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.MobileMessageType;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.SmsCodeTokenConstant;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.sms.SmsCodeAuthenticationToken;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 手机号验证码登录TokenGranter
 *
 */
public class SmsCodeTokenGranter extends AbstractTokenGranter {


	private final AuthenticationManager authenticationManager;


	public SmsCodeTokenGranter(AuthenticationManager authenticationManager,
                               AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory) {
		this(authenticationManager, tokenServices, clientDetailsService, requestFactory, SmsCodeTokenConstant.GRANT_TYPE);
	}

	protected SmsCodeTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                  ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		String mobile = parameters.get(SmsCodeTokenConstant.SPRING_SECURITY_RESTFUL_PHONE_KEY);
		String code = parameters.get(SmsCodeTokenConstant.SPRING_SECURITY_RESTFUL_VERIFY_CODE_KEY);

		if (StrUtil.isBlank(code)) {
			throw new UserDeniedAuthorizationException("请输入验证码！");
		}

		String codeFromRedis = null;
		// 从Redis里读取存储的验证码信息
		String key = String.format("%s%s_%s", SmsCodeTokenConstant.VERIFICATION_CODE_PREFIX, MobileMessageType.LOGIN_VERIFICATION_CODE.getValue(), mobile);
		if(!RedisUtils.hasKey(key)){
			throw new UserDeniedAuthorizationException("验证码不存在！");
		}
		String values = String.valueOf(RedisUtils.get(key));
		if (StringUtils.isEmpty(values)) {
			throw new UserDeniedAuthorizationException("验证码已过期！");
		}
		// 比较输入的验证码是否正确
		if (!StrUtil.equalsIgnoreCase(code, values)) {
			throw new UserDeniedAuthorizationException("验证码不正确！");
		}

		RedisUtils.del(key);

		Authentication userAuth = new SmsCodeAuthenticationToken(mobile);
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);
		try {
			userAuth = authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException | BadCredentialsException ase) {
			//covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
			throw new InvalidGrantException(ase.getMessage());
		}
		// If the username/password are wrong the spec says we should send 400/invalid grant

		if (userAuth == null || !userAuth.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate user: " + mobile);
		}

		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}
}
