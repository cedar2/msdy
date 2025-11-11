package com.platform.ems.util;

import com.platform.common.core.domain.EmsBaseEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author chenkw
 */
public class LightUtil {

    /**
     * 空白
     */
    public static final String LIGHT_NULL = "-1";

    /**
     * 红灯
     */
    public static final String LIGHT_RED = "0";

    /**
     * 绿灯
     */
    public static final String LIGHT_GREEN = "1";

    /**
     * 黄橙
     */
    public static final String LIGHT_YELLOW = "2";

    /**
     * 蓝灯
     */
    public static final String LIGHT_BLUE = "3";

    /**
     * 灰色 未开始
     */
    public static final String LIGHT_GRY = "4";

    /**
     * 灰色 取消
     */
    public static final String LIGHT_GRY_ZG = "5";

    /**
     * 灰色 暂搁
     */
    public static final String LIGHT_GRY_QX = "6";

    /**
     * endDate 数据带的截止时间，  date 一般是当前时间
     */
    public static void setLight(EmsBaseEntity entity, Date endDate, LocalDateTime date, Integer expireDays){
        if (date == null) {
            LocalDate localDate = LocalDate.now();
            date = localDate.atStartOfDay();
        }
        if (endDate == null) {
            // 空白
            entity.setLight(LIGHT_NULL);
        }
        else {
            LocalDateTime ldt1 = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (ldt1.isBefore(date)) {
                // 红灯
                entity.setLight(LIGHT_RED);
            }
            else {
                // 注意比较是连时间都比较的
                Duration duration = Duration.between(ldt1, date);
                // 计算天数差
                long duringDay = duration.toDays();
                long days = expireDays == null ? 0 : (long)expireDays;
                if (Math.abs(duringDay) > days) {
                    // 绿灯
                    entity.setLight(LIGHT_GREEN);
                }
                else {
                    // 橙灯
                    entity.setLight(LIGHT_YELLOW);
                }
            }
        }
    }

}
