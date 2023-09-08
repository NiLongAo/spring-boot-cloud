package cn.com.tzy.springbootcomm.common.vo;

/**
 * @author TZY
 */

public enum RespCode {
    /**
     * 相应状态码
     */
    CODE_0(0, "成功"),
    CODE_1(1, "服务器内部错误"),
    CODE_2(2, "参数错误"),

    CODE_101(101, "接口限流了"),
    CODE_102(102, "服务降级了"),
    CODE_103(103, "热点参数限流了"),
    CODE_104(104, "系统规则（负载/...不满足要求）"),
    CODE_105(105, "授权规则不通过"),

    CODE_310(310, "密码未设置"),
    CODE_311(311, "用户名或密码错误"),
    CODE_312(312, "用户输入密码次数超限"),
    CODE_313(313, "客户端认证失败"),
    CODE_314(314, "token无效或已过期"),
    CODE_315(315, "token已被禁止访问"),
    CODE_316(316, "访问权限异常"),
    CODE_317(317, "访问未授权"),
    CODE_318(318, "访问重复请求"),
    CODE_319(319, "账号锁定"),
    ;

    private final int value;
    private final String name;

    RespCode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
