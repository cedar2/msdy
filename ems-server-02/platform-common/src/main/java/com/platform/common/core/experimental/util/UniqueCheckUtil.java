package com.platform.common.core.experimental.util;

import com.platform.common.exception.CheckedException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Straw
 * @date 2022/12/20
 */
public class UniqueCheckUtil {

    /**
     * @param givenCase            被检验的【原对象】。（通常是前端传来的参数）
     * @param resultCaseListGetter 获得【比较对象】列表的getter，这些【比较对象】会和【原对象】对比
     * @param uniqueFieldGetter    可以证明一个【比较对象】和一个【原对象】是相同的字段的getter。（通常是比较sid）
     * @param errorMsg             抛出 CheckedException 时携带的 message
     */
    public static <T> void checkUnique(T givenCase,
                                       Supplier<List<T>> resultCaseListGetter,
                                       Function<T, Object> uniqueFieldGetter,
                                       String errorMsg) {
        List<T> resultCaseList = resultCaseListGetter.get();
        if (resultCaseList == null || resultCaseList.isEmpty()) {
            // 不存在这个人，说明是新建的
            return;
        }

        if (resultCaseList.size() != 1) {
            // 存在多个这个 叫这个名字的人，有问题
            throw new CheckedException(errorMsg);
        }

        T onlyOne = resultCaseList.get(0);

        if (uniqueFieldGetter.apply(onlyOne).equals(uniqueFieldGetter.apply(givenCase))) {
            // 这两个是同一个人，这没问题。
            return;
        }

        // 这两个不是同一个人，但叫了同样名字，这是有问题的。
        throw new CheckedException(errorMsg);
    }
}

