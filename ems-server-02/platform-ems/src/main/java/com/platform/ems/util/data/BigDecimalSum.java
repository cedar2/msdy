package com.platform.ems.util.data;

import java.math.BigDecimal;

public class BigDecimalSum {

    public static BigDecimal ifNull(BigDecimal value) {
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static Long ifNull(Long value) {
        if (value != null) {
            return value;
        } else {
            return new Long(0);
        }
    }

    public static BigDecimal sum(BigDecimal ...value){
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < value.length; i++){
            result = result.add(ifNull(value[i]));
        }
        return result;
    }

    public static Long sum(Long ...value) {
        Long result = new Long(0);
        for (int i = 0; i < value.length; i++){
            result = result + ifNull(value[i]);
        }
        return result;
    }
}
