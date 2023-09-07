package cn.com.tzy.springbootstarterlogscore.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.log4j.Log4j2;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * IP工具类
 * 离线ipk库 https://gitee.com/lionsoul/ip2region/tree/master/data
 * @author pangu
 */
@Log4j2
public class IPUtil {

	private final static boolean ipLocal = false;

	public static String getIp(HttpServletRequest request) {
		String ipAddress = null;
		try {
			ipAddress = request.getHeader("x-forwarded-for");
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress.equals("127.0.0.1")) {
					// 根据网卡取本机配置的IP
					InetAddress inet = null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress = inet.getHostAddress();
				}
			}
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
				// = 15
				if (ipAddress.indexOf(",") > 0) {
					ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
				}
			}
		} catch (Exception e) {
			ipAddress="";
		}
		// ipAddress = this.getRequest().getRemoteAddr();
		return ipAddress;
	}


	/**
	 * 根据ip获取ip详细地址
	 * @return
	 */
	public static String getIpAdder(String ip) {
		Searcher searcher = SpringUtil.getBean(Searcher.class);
		String search = "";
		try {
			search = searcher.search(ip);
		}catch (Exception e){
			log.error("数据异常:",e);
		}
		String[] split = search.split("\\|");
		List<String> adderList = new ArrayList<>();
		for (String s : split) {
			if("0".equals(s)){
				continue;
			}
			adderList.add(s);
		}
		return String.join("-", adderList);
	}
}
