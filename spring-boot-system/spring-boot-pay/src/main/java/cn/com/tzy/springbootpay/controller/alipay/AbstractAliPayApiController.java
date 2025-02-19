package cn.com.tzy.springbootpay.controller.alipay;

import cn.com.tzy.springbootstarterpayalipay.config.AliPayApiConfig;
import com.alipay.api.AlipayApiException;

/**
 * @author Javen
 */
public abstract class AbstractAliPayApiController {
	/**
	 * 获取支付宝配置
	 *
	 * @return {@link AliPayApiConfig} 支付宝配置
	 * @throws AlipayApiException 支付宝 Api 异常
	 */
	public abstract AliPayApiConfig getApiConfig() throws AlipayApiException;
}
