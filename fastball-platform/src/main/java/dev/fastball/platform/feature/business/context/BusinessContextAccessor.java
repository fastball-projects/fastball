package dev.fastball.platform.feature.business.context;


import java.util.Collection;

/**
 * @author Geng Rong
 */
public interface BusinessContextAccessor<T extends BusinessContextItem> {

    /**
     * 业务上下文唯一标识
     *
     * @return 业务上下文唯一标识
     */
    String contextKey();

    /**
     * 获取业务上下文列表
     *
     * @return 业务上下文列表
     */
    Collection<T> listBusinessContextItems();

    /**
     * 根据业务上下文ID设置业务上下文
     * 会将业务上下文设置到当前线程的ThreadLocal中
     * 同时可以做上下文是否存在, 当前用户是否可以使用该上下文的校验
     * 也可以在这里做一些业务上下文设置之前的操作
     *
     * @param businessContextId 业务上下文ID
     */
    BusinessContextItem getBusinessContextById(String businessContextId);

    /**
     * 在业务上下文清除之前, 可以在这里做一些业务上下文清除之前的操作
     */
    default void beforeBusinessContextClean() {
    }
}
