package com.platform.common.redis.lock;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CheckedException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.function.Function;

/**
 * @author Straw
 * @since 2023/3/31
 */
public abstract class UniqueLockUtil {

    volatile static StringRedisTemplate redis;

    public static void checkBean() {
        if (redis == null) {
            redis = SpringUtil.getBean(StringRedisTemplate.class);
        }
    }

    public static <T> UniqueLocker<T> getLocker(String hashKey,
                                                Function<T, ?> fieldValueGetter,
                                                String errorMsg) {
        return new RedisUniqueLockerImpl<>(hashKey, errorMsg, fieldValueGetter);
    }

    @AllArgsConstructor
    static class RedisUniqueLockerImpl<T> implements UniqueLocker<T> {
        String hashKey;
        String errorMsg;

        Function<T, ?> fieldValueGetter;

        public void lockUnique(T entity) throws CheckedException {
            checkBean();
            Boolean absent = redis.opsForHash().putIfAbsent(hashKey,
                    fieldValueGetter.apply(entity),
                    "true");
            if (BooleanUtil.isFalse(absent)) {
                throw new CheckedException(errorMsg);
            }
        }

        public void unlockUnique(T entity) throws CheckedException {
            checkBean();
            redis.opsForHash().delete(hashKey, fieldValueGetter.apply(entity));
        }

        public void unlockUniqueBatch(List<T> list) throws CheckedException {
            checkBean();
            redis.opsForHash().delete(hashKey, list.stream().map(fieldValueGetter).toArray());
        }


    }

}
