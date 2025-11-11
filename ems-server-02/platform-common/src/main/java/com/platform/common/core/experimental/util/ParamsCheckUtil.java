package com.platform.common.core.experimental.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;

import java.util.function.Function;

/**
 * @author Straw
 * @date 2023/1/30
 */
public class ParamsCheckUtil {

    /**
     * 修改启停状态的参数校验
     */
    public static <T> void checkStatus(T o,
                                       Function<T, String> statusGetter,
                                       Function<T, Long[]> sidArrayGetter) {
        if (o == null) {
            throw new CheckedException("空数据");
        }

        if (ArrayUtil.isEmpty(sidArrayGetter.apply(o))) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(statusGetter.apply(o))) {
            throw new CheckedException("参数缺失");
        }

    }
}
