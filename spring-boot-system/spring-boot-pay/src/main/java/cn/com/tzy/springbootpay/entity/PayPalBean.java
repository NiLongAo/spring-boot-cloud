package cn.com.tzy.springbootpay.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>PayPal配置 Bean</p>
 *
 * @author Javen
 */
@Component
@PropertySource("classpath:/paypal.properties")
@ConfigurationProperties(prefix = "paypal")
public class PayPalBean {
	private String clientId;
	private String secret;
	private Boolean sandBox;
	private String domain;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Boolean getSandBox() {
		return sandBox;
	}

	public void setSandBox(Boolean sandBox) {
		this.sandBox = sandBox;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "PayPalBean{" +
			"clientId='" + clientId + '\'' +
			", secret='" + secret + '\'' +
			", sandBox=" + sandBox +
			", domain='" + domain + '\'' +
			'}';
	}
}
