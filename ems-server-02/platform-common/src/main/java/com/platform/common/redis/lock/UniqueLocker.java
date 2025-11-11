package com.platform.common.redis.lock;


import com.platform.common.exception.CheckedException;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Straw
 * @since 2023/3/31
 */
public interface UniqueLocker<T> {
    void lockUnique(T entity) throws CheckedException;

    void unlockUnique(T entity) throws CheckedException;

    default void unlockUniqueBatch(List<T> list) {
        list.forEach(this::unlockUnique);
    }

    default <K> K updateUnique(T theNew,
                               T theOld,
                               Supplier<K> doService) {
        // 尝试更新，新记录加锁
        this.lockUnique(theNew);

        try {
            K t = doService.get();
            // 更新成功，对旧记录解锁
            this.unlockUnique(theOld);

            return t;
        } catch (Exception e) {
            // 更新失败，对新记录解锁
            this.unlockUnique(theNew);
            throw e;
        }
    }

}
