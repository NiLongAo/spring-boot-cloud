package cn.com.tzy.springbootactiviti.wrapper;

/**
 * Predicate扩展，可抛异常，一般用于lambda操作
 *
 * @param <T> 断言入参类型
 * @param <E> 可抛异常类型
 * @author yiuman
 * @date 2020/5/26
 */
public interface PredicateWrapper<T, E extends Exception> {

    /**
     * 断言
     *
     * @param t 参数
     * @return 断言结果true/false
     * @throws E 抛出的异常
     */
    boolean test(T t) throws E;
}