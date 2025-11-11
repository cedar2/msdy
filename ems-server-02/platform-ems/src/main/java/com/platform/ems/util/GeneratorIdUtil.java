package com.platform.ems.util;

import com.platform.common.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author linhongwei
 * @Description 获取生成唯一id
 * @Date 13:30
 * @Param
 * @return
 **/
@SuppressWarnings("all")
public class GeneratorIdUtil {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final AtomicInteger atomicInteger = new AtomicInteger(1000000);
    //业务
    public static final String CenterNoYW = "YW";
    //文件
    public static final String CenterNoWJ = "WJ";
    //充值
    public static final String CenterNoCZ = "CZ";

    /**
     * 创建不连续的订单号
     *
     * @param no 数据中心编号
     * @return 唯一的、不连续订单号
     */
    public static synchronized String getOrderNoByUUID(String no) {
        Integer uuidHashCode = UUID.randomUUID().toString().hashCode();
        if (uuidHashCode < 0) {
            uuidHashCode = uuidHashCode * (-1);
        }
        String date = simpleDateFormat.format(new Date());
        return no + date + uuidHashCode;
    }

    /**
     * 获取同一秒钟 生成的订单号连续
     *
     * @param no 数据中心编号
     * @return 同一秒内订单连续的编号
     */
    public static synchronized String getOrderNoByAtomic(String no) {
        atomicInteger.getAndIncrement();
        int i = atomicInteger.get();
        String date = simpleDateFormat.format(new Date());
        return no + date + i;
    }

    /**
     * 得到n位长度的随机数
     *
     * @param n 随机数的长度
     * @return 返回 n位的随机整数
     */
    public static int getRandomNumber(int n) {
        int temp = 0;
        int min = (int)Math.pow(10, n - 1);
        int max = (int)Math.pow(10, n);
        Random rand = new Random();
        /* 确保生成的四位随机数后即返回 */
        while (true) {
            temp = rand.nextInt(max);
            if (temp >= min) {
                break;
            }

        }
        return temp;
    }

    /**
     * @param
     * @return String
     * @Description: 产生订单编号随机数
     */
    public static String getRandomOrderId() {
        // 当前日期
        Date date = new Date();
        String dString = DateUtils.parseDateToStr("yyyyMMddHHmmssSSS", date);
        // 产生三位的随机数
        int rndCount = (int)(Math.random() * 900) + 100;
        String random = dString + Integer.toString(rndCount);
        return random;
    }

    /**
     * 产生随机id
     * @param prefix id前缀
     * @return
     */
    public static String getRandomId(String prefix) {
        // 当前日期
        Date date = new Date();
        String dString = DateUtils.parseDateToStr("yyyyMMddHHmmss", date);
        // 产生三位的随机数
        int rndCount = (int)(Math.random() * 900) + 100;
        String random = dString + Integer.toString(rndCount);
        return prefix+random;
    }


    /**
     * @param
     * @return String
     * @Description: 产生订单编号随机数
     */
    public static String getRandomOrderId(String prefix) {
        return prefix + getRandomOrderId();
    }

}
