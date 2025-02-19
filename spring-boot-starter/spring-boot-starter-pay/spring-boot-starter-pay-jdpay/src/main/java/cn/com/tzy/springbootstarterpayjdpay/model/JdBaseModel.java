
package cn.com.tzy.springbootstarterpayjdpay.model;

import cn.com.tzy.springbootstarterpayjdpay.kit.JdPayKit;
import cn.hutool.core.util.StrUtil;
import cn.com.tzy.springbootstarterpaystarter.BaseModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付等常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>Model 公用方法</p>
 *
 * @author Javen
 */
public class JdBaseModel extends BaseModel {
	/**
	 * 自动生成请求接口的 xml
	 *
	 * @param rsaPrivateKey RSA 私钥
	 * @param strDesKey     DES 密钥
	 * @param version       版本号
	 * @param merchant      商户号
	 * @return 生成的 xml 数据
	 */
	public String genReqXml(String rsaPrivateKey, String strDesKey, String version, String merchant) {

		if (StrUtil.isEmpty(version) || StrUtil.isEmpty(merchant)) {
			throw new RuntimeException("version or merchant is empty");
		}
		String encrypt = JdPayKit.encrypt(rsaPrivateKey, strDesKey, JdPayKit.toJdXml(toMap()));
		Map<String, String> requestMap = JdRequestModel.builder()
			.version(version)
			.merchant(merchant)
			.encrypt(encrypt)
			.build()
			.toMap();
		return JdPayKit.toJdXml(requestMap);
	}

	/**
	 * PC H5 支付创建签名
	 *
	 * @param rsaPrivateKey RSA 私钥
	 * @param strDesKey     DES 密钥
	 * @return 生成签名后的 Map
	 */
	public Map<String, String> createSign(String rsaPrivateKey, String strDesKey) {
		Map<String, String> map = toMap();
		// 生成签名
		String sign = JdPayKit.signRemoveSelectedKeys(map, rsaPrivateKey, new ArrayList<String>());
		map.put("sign", sign);
		// 3DES进行加密
		return JdPayKit.threeDesToMap(map, strDesKey);
	}
}
