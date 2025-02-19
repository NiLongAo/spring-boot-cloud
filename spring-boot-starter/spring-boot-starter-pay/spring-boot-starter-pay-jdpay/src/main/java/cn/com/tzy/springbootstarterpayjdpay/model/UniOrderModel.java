/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p> 统一下单接口 </p>
 *
 * @author Javen
 */
package cn.com.tzy.springbootstarterpayjdpay.model;

import lombok.*;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UniOrderModel extends JdBaseModel {
	private String version;
	private String sign;
	private String merchant;
	private String payMerchant;
	private String device;
	private String tradeNum;
	private String tradeName;
	private String tradeDesc;
	private String tradeTime;
	private String amount;
	private String orderType;
	private String industryCategoryCode;
	private String currency;
	private String note;
	private String callbackUrl;
	private String notifyUrl;
	private String ip;
	private String specCardNo;
	private String specId;
	private String specName;
	private String userId;
	private String tradeType;
	private String expireTime;
	private String orderGoodsNum;
	private String vendorId;
	private String goodsInfo;
	private String receiverInfo;
	private String termInfo;
	private String riskInfo;
	private String installmentNum;
	private String preProduct;
	private String bizTp;
}
