package cn.com.tzy.springbootstarterpaypal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付等常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>AccessTokenCache 默认缓存实现，默认存储与内存中</p>
 *
 * @author Javen
 */
public class DefaultAccessTokenCache implements IAccessTokenCache {

	private final Map<String, String> map = new ConcurrentHashMap<>();

	@Override
	public String get(String key) {
		return map.get(key);
	}

	@Override
	public void set(String key, String jsonValue) {
		map.put(key, jsonValue);
	}

	@Override
	public void remove(String key) {
		map.remove(key);
	}

}
