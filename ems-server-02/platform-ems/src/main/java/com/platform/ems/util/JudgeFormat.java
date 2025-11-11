package com.platform.ems.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yang
 * 格式校验
 **/
public class JudgeFormat {

    /**
     * 校验是否是正整数
     */
    public static boolean isPositiveInteger(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        try {
            int num = Integer.parseInt(str);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 检查一个字符串是否为一个有效的数值，并且它的整数位和小数位是否符合指定的最大值限制
     * @param maxPos 整数位最大长度限制
     * @param maxDec 小数位最大长度限制
     */
    public static boolean isValidNumericValueWithinRange(String str, int maxPos, int maxDec) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        try {
            BigDecimal decimal = new BigDecimal(str);
            int integerLen = decimal.precision() - decimal.scale();
            int decimalLen = Math.max(decimal.scale(), 0);
            if (integerLen > maxPos || decimalLen > maxDec) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 要求输入的字符串都是数字
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 要求输入的字符串都是数字或字母
     */
    public static boolean isNumericOrLetter(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验输入日期的合法性 分隔符只能是 “-” 或者 “/”
     */
    public static boolean isValidDateFormat(String dateStr) {
        if (dateStr == null) {
            return false;
        }
        String regex = "^\\d{4}[-/]?\\d{1,2}[-/]?\\d{1,2}$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(dateStr).matches()) {
            return false;
        }
        try {
            // 使用Java 8的日期API进行解析和验证
            DateUtil.parse(dateStr);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 要求输入的所属年月分隔符只能是 “-” 或者 “/”
     */
    public static boolean isYearMonth(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        String regex = "^(19|20)\\d{2}[-/]((0?[1-9])|(1[0-2]))$";
        if (!str.matches(regex)) {
            return false;
        }
        return true;
    }

    /**
     * 字符串是否符合中国的手机号格式
     */
    public static boolean isValidChineseMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.length() != 11) {
            return false;
        }
        // 中国移动
        String cmccRegex = "^1(34[0-8]|3[5-9]\\d|4[7-8]\\d|5[0-27-9]\\d|7[8]\\d|8[2-478]\\d)\\d{7}$";
        // 中国联通
        String cuccRegex = "^1(3[0-2]|4[5-6]|5[56]|7[56]|8[56])\\d{8}$";
        // 中国电信
        String ctcRegex = "^1(33|49|53|7[37]|8[019])\\d{8}$";
        // 匹配任意一个运营商即可
        return mobileNumber.matches(cmccRegex) || mobileNumber.matches(cuccRegex) || mobileNumber.matches(ctcRegex);
    }

    /**
     * 判断输入的是否符合邮箱地址格式
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/M/dd"),
            DateTimeFormatter.ofPattern("yyyy-M-dd"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy/MM/d"),
            DateTimeFormatter.ofPattern("yyyy-MM-d"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    //校验输入日期的合法性
    public static boolean isValidDate(String str) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate.parse(str, formatter);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }
    /**
     *  校验输入的是否是数字(小数)
     */
    public static boolean isValidDouble(String str) {
        boolean convertSuccess = true;
        try {
            Double.valueOf(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    //校验输入的是否是正整数
    public static boolean isValidInt(String str) {
        boolean convertSuccess = true;
        try {
            Integer.valueOf(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
    //校验手机格式
    public static boolean isPhone(String phone){
        String regex =  "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,1-9]))\\d{8}$";
        if (Pattern.matches(regex, phone)) {
            return true;
        } else {
            return false;
        }
    }
    //校验是数字和字母
    public static boolean isCodeType(String str){
        String letter = "[a-zA-Z]";
        String number = "[0-9]";
        Pattern p = Pattern.compile(letter);
        Pattern p2 = Pattern.compile(number);
        String[] strings = str.split("");
        for (String c : strings) {
            Matcher m = p.matcher(c);
            Matcher m2 = p2.matcher(c);
            if(!m.find()&&!m2.find()){
                return false;
            }
        }
        return true;
    }
    //校验是数字和字母和-
    public static boolean isNumberOrE(String str){
        String letter = "[a-zA-Z]";
        String number = "[0-9]";
        String symbol = "-";
        Pattern p = Pattern.compile(letter);
        Pattern p2 = Pattern.compile(number);
        Pattern p3 = Pattern.compile(symbol);
        String[] strings = str.split("");
        for (String c : strings) {
            Matcher m = p.matcher(c);
            Matcher m2 = p2.matcher(c);
            Matcher m3 = p3.matcher(c);
            if(!m.find()&&!m2.find()&&!m3.find()){
                return false;
            }
        }
        return true;
    }
    //邮箱
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     *   校验输入的是否是正小数,长度
     *   maxPos:整数位最大长度
     *   maxDec:小数位长度
     */
    public static boolean isValidDouble(String str,int maxPos,int maxDec) {
        if (StrUtil.isBlank(str)){
            return false;
        }
        boolean convertSuccess = true;
        try {
            //
            Double.valueOf(str);
            String[] split = str.split("\\.");
            if (split.length != 1){
                if (split[0].length() > maxPos){
                    return false;
                }
                if (split[1].length() > maxDec){
                    return false;
                }
            }
            else if (split.length == 1){
                if (split[0].length() > maxPos){
                    return false;
                }
            }
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     *   校验输入的是否是正小数,长度
     *   maxPos:整数位最大长度
     */
    public static boolean isValidDouble(String str,int maxPos) {
        if (StrUtil.isBlank(str)){
            return false;
        }
        boolean convertSuccess = true;
        try {
            Double.valueOf(str);
            String[] split = str.split("\\.");
            if (split.length != 1){
                if (split[0].length() > maxPos){
                    return false;
                }
            }
            else if (split.length == 1){
                if (split[0].length() > maxPos){
                    return false;
                }
            }
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     *   校验输入的是否是正小数,长度
     *   maxPos:整数位最大长度
     *   maxDec:小数位长度
     */
    public static boolean isValidDoubleLgZero(String str,int maxPos,int maxDec) {
        if (StrUtil.isBlank(str)){
            return false;
        }
        boolean convertSuccess = true;
        try {
            //
            Double quantity = Double.valueOf(str);
            if(quantity<=0){
                return false;
            }
            String[] split = str.split("\\.");
            if (split.length != 1){
                if (split[0].length() > maxPos){
                    return false;
                }
                if (split[1].length() > maxDec){
                    return false;
                }
            }
            else if (split.length == 1){
                if (split[0].length() > maxPos){
                    return false;
                }
            }
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

}
