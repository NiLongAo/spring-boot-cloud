package cn.com.tzy.springbootstarterpaywxpay.enums.v3;

import cn.com.tzy.springbootstarterpaywxpay.enums.WxApiEnum;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 * <p>IJPay 自由交流群: 864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>微信支付 v3 接口-支付即服务接口枚举</p>
 *
 * @author Javen
 */
public enum SmartGuideApiEnum implements WxApiEnum {
	/**
	 * 服务人员注册/查询
	 */
	SMART_GUIDE("/v3/smartguide/guides", "服务人员注册/查询"),

	/**
	 * 服务人员分配
	 */
	SMART_GUIDE_ASSIGN("/v3/smartguide/guides/%s/assign", "服务人员分配"),

	/**
	 * 服务人员信息更新
	 */
	SMART_GUIDE_UPDATE("/v3/smartguide/guides/%s", "服务人员信息更新"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	SmartGuideApiEnum(String url, String desc) {
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
