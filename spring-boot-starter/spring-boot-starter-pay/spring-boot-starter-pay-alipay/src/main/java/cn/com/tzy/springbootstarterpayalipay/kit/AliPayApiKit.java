package cn.com.tzy.springbootstarterpayalipay.kit;

import cn.com.tzy.springbootstarterpayalipay.config.AliPayConfig;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AliPayApiKit {
    private static final ThreadLocal<String> TL = new ThreadLocal<String>();

    private static final Map<String, AliPayConfig> CFG_MAP = new ConcurrentHashMap<String, AliPayConfig>();
    private static final String DEFAULT_CFG_KEY = "_default_key_";

    /**
     * <p>向缓存中设置 AliPayModel </p>
     * <p>每个 appId 只需添加一次，相同 appId 将被覆盖</p>
     *
     * @param AliPayConfig 支付宝支付配置
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig putApiConfig(AliPayConfig AliPayConfig) {
        if (CFG_MAP.size() == 0) {
            CFG_MAP.put(DEFAULT_CFG_KEY, AliPayConfig);
        }
        CFG_MAP.put(AliPayConfig.getAppId(), AliPayConfig);
        return AliPayConfig;
    }

    /**
     * 向当前线程中设置 {@link AliPayConfig}
     *
     * @param AliPayConfig {@link AliPayConfig} 支付宝配置对象
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig setThreadLocalAliPayModel(AliPayConfig AliPayConfig) {
        if (StrUtil.isNotEmpty(AliPayConfig.getAppId())) {
            setThreadLocalAppId(AliPayConfig.getAppId());
        }
        return putApiConfig(AliPayConfig);
    }

    /**
     * 通过 AliPayModel 移除支付配置
     *
     * @param AliPayConfig {@link AliPayConfig} 支付宝配置对象
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig removeApiConfig(AliPayConfig AliPayConfig) {
        return removeApiConfig(AliPayConfig.getAppId());
    }

    /**
     * 通过 appId 移除支付配置
     *
     * @param appId 支付宝应用编号
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig removeApiConfig(String appId) {
        return CFG_MAP.remove(appId);
    }

    /**
     * 向当前线程中设置 appId
     *
     * @param appId 支付宝应用编号
     */
    public static void setThreadLocalAppId(String appId) {
        if (StrUtil.isEmpty(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        TL.set(appId);
    }

    /**
     * 移除当前线程中的 appId
     */
    public static void removeThreadLocalAppId() {
        TL.remove();
    }

    /**
     * 获取当前线程中的  appId
     *
     * @return 支付宝应用编号 appId
     */
    public static String getAppId() {
        String appId = TL.get();
        if (StrUtil.isEmpty(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        return appId;
    }

    /**
     * 获取当前线程中的 AliPayModel
     *
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig getAliPayModel() {
        String appId = getAppId();
        return getApiConfig(appId);
    }

    /**
     * 通过 appId 获取 AliPayModel
     *
     * @param appId 支付宝应用编号
     * @return {@link AliPayConfig}
     */
    public static AliPayConfig getApiConfig(String appId) {
        AliPayConfig cfg = CFG_MAP.get(appId);
        if (cfg == null) {
            throw new IllegalStateException("需事先调用 AliPayModelKit.putApiConfig(AliPayModel) 将 appId对应的 AliPayModel 对象存入，才可以使用 AliPayModelKit.getAliPayModel() 的系列方法");
        }
        return cfg;
    }
}
