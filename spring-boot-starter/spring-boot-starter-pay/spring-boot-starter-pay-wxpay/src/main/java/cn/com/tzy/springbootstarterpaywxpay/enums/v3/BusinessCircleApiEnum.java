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
 * <p>微信支付 v3 接口-智慧商圈接口枚举</p>
 *
 * @author Javen
 */
public enum BusinessCircleApiEnum implements WxApiEnum {

	/**
	 * 智慧商圈-商圈积分同步
	 */
	BUSINESS_CIRCLE_POINTS_NOTIFY("/v3/businesscircle/points/notify", "商圈会员积分同步"),

	/**
	 * 智慧商圈-商圈积分授权查询
	 */
	BUSINESS_CIRCLE_USER_AUTHORIZATIONS("/v3/businesscircle/user-authorizations/%s", "商圈积分授权查询"),

	/**
	 * 智慧商圈-商圈会员待积分状态查询
	 */
	BUSINESS_CIRCLE_COMMIT_STATUS("/v3/businesscircle/users/%s/points/commit_status", "商圈会员待积分状态查询"),

	/**
	 * 智慧商圈-商圈会员停车状态同步
	 */
	BUSINESS_CIRCLE_PARKING("v3/businesscircle/parkings", "商圈会员停车状态同步"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	BusinessCircleApiEnum(String url, String desc) {
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
