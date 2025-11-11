package com.platform.ems.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法单例工具类
 * @author cwp
 */
public class SnowFlakeUtil {
    private volatile static SnowFlakeUtil snowFlakeUtil;

    private long workerId = 0;

    private long datacenterId = 1;

    private Snowflake snowflake = IdUtil.createSnowflake(workerId,datacenterId);

    private SnowFlakeUtil(){}

    public static SnowFlakeUtil getSnowFlake() {
        if (snowFlakeUtil == null) {
            synchronized (SnowFlakeUtil.class) {
                if (snowFlakeUtil == null) {
                    snowFlakeUtil = new SnowFlakeUtil();
                }
            }
        }
        return snowFlakeUtil;
    }

    public long getNextId(){
        return snowflake.nextId();
    }

    public String getstringNextId(){
        return String.valueOf(snowflake.nextId());
    }
}
