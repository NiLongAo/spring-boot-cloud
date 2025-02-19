package cn.com.tzy.springbootstarterpayxpay;

import cn.com.tzy.springbootstarterpayxpay.interfaces.PayUrl;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.kit.HttpKit;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>微信官方个人支付渠道，有稳定的异步通知，加企鹅(572839485)了解更多</p>
 *
 * <p>XPay Api</p>
 *
 * @author Javen
 */
public class PayApi {
	/**
	 * 获取接口请求的 URL
	 *
	 * @param payUrl    {@link PayUrl} 支付 API 接口枚举
	 * @param serverUrl 网关
	 * @return {@link String} 返回完整的接口请求URL
	 */
	public static String getReqUrl(String serverUrl, PayUrl payUrl) {
		return serverUrl.concat(payUrl.getUrl());
	}

	/**
	 * post 请求
	 *
	 * @param url     请求 url
	 * @param params  {@link Map} 请求参数
	 * @param headers {@link Map} 请求头
	 * @return {@link IJPayHttpResponse} 请求返回的结果
	 */
	public static IJPayHttpResponse post(String url, Map<String, Object> params, Map<String, String> headers) {
		return HttpKit.getDelegate().post(url, params, headers);
	}

	/**
	 * get 请求
	 *
	 * @param url     请求 url
	 * @param params  {@link Map} 请求参数
	 * @param headers {@link Map} 请求头
	 * @return {@link IJPayHttpResponse} 请求返回的结果
	 */
	public static IJPayHttpResponse get(String url, Map<String, Object> params, Map<String, String> headers) {
		return HttpKit.getDelegate().get(url, params, headers);
	}

	/**
	 * 执行请求
	 *
	 * @param serverUrl XPay 网关
	 * @param payUrl    {@link PayUrl} 支付 API 接口枚举
	 * @param params    请求参数
	 * @return {@link IJPayHttpResponse} 请求返回的结果
	 */
	public static IJPayHttpResponse exePost(String serverUrl, PayUrl payUrl, Map<String, String> params) {
		Map<String, Object> dataMap = new HashMap<String, Object>(params);
		return post(getReqUrl(serverUrl, payUrl), dataMap, null);
	}

	/**
	 * 执行请求
	 *
	 * @param serverUrl XPay 网关
	 * @param payUrl    {@link PayUrl} 支付 API 接口枚举
	 * @param params    请求参数
	 * @return {@link IJPayHttpResponse} 请求返回的结果
	 */
	public static IJPayHttpResponse exeGet(String serverUrl, PayUrl payUrl, Map<String, String> params) {
		Map<String, Object> dataMap = new HashMap<String, Object>(params);
		return get(getReqUrl(serverUrl, payUrl), dataMap, null);
	}
}
