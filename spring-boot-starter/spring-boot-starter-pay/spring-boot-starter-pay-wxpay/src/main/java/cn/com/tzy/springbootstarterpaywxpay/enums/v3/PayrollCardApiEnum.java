package cn.com.tzy.springbootstarterpaywxpay.enums.v3;

import cn.com.tzy.springbootstarterpaywxpay.enums.WxApiEnum;


/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>微信支付 v3 接口-务工卡接口相关枚举</p>
 *
 * @author Javen
 */
public enum PayrollCardApiEnum implements WxApiEnum {

	/**
	 * 生成授权token
	 */
	TOKEN("/v3/payroll-card/tokens", "生成授权token"),

	/**
	 * 查询务工卡授权关系
	 */
	RELATION("/v3/payroll-card/relations/%s", "查询务工卡授权关系"),

	/**
	 * 务工卡核身预下单
	 */
	AUTHENTICATION_PRE_ORDER("/v3/payroll-card/authentications/pre-order", "务工卡核身预下单"),

	/**
	 * 获取核身结果
	 */
	AUTHENTICATION_RESULT("/v3/payroll-card/authentications/%s", "获取核身结果"),

	/**
	 * 查询核身记录
	 */
	AUTHENTICATION_LIST("/v3/payroll-card/authentications", "查询核身记录"),

	/**
	 * 务工卡核身预下单（流程中完成授权）
	 */
	PRE_ORDER_WITH_AUTH("/v3/payroll-card/authentications/pre-order-with-auth", "务工卡核身预下单（流程中完成授权）"),

	/**
	 * 发起批量转账
	 */
	BATCH_TRANSFER("/v3/payroll-card/transfer-batches", "发起批量转账"),




	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	PayrollCardApiEnum(String url, String desc) {
		this.url = url;
		this.desc = desc;
	}

	/**
	 * 获取枚举URL
	 *
	 * @return 枚举编码
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/**
	 * 获取详细的描述信息
	 *
	 * @return 描述信息
	 */
	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return url;
	}
}
