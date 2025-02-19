package cn.com.tzy.springbootstarterpaywxpay.model.v3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>V3 微信申请退款 Model</p>
 *
 * @author Javen
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class RefundModel {
	/**
	 * 子商户号
	 */
	private String sub_mchid;
	/**
	 * 微信支付订单号
	 */
	private String transaction_id;
	/**
	 * 商户订单号
	 */
	private String out_trade_no;
	/**
	 * 商户退款单号
	 */
	private String out_refund_no;
	/**
	 * 退款原因
	 */
	private String reason;
	/**
	 * 退款结果回调url
	 */
	private String notify_url;
	/**
	 * 退款资金来源
	 */
	private String funds_account;
	/**
	 * 金额信息
	 */
	private RefundAmount amount;
	/**
	 * 退款商品
	 */
	private List<RefundGoodsDetail> goods_detail;
}


