package com.platform.common.utils.bean;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.document.OperMsg;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 *
 * @author platform
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     *
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     *
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     *
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     *
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }


    public static String findIdName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String idName = "";
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                idName = field.getName();
            }
            continue;
        }
        return idName;
    }

    public static Object getValue(Object o, String fieldName) {
        Object value = new Object();
        try {
            String Name = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method m = o.getClass().getMethod("get" + Name);
            value = m.invoke(o);
        } catch (Exception e) {
            e.getMessage();
        }
        return value;
    }

    public static void setValue(Object o, String fieldName ,Object value) {
        try {
            String Name = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getMethod = o.getClass().getMethod("get" + Name);
            Class<?> aClass = o.getClass();
            Method setMethod= aClass.getMethod("set" + Name, getMethod.getReturnType());
            setMethod.invoke(o,value);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static List<Field> getAllField(Object model) {
        Class clazz = model.getClass();
        List<Field> fields = new ArrayList<>();
        //只要父类存在，就获取其类的属性到集合
        while (clazz != null) {
            fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            //获取其父类
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 修改注解上的值
     * @param o 注解
     * @param name 字段名称
     * @param value 字段值
     * @return
     */
    public static Boolean setAnnotationValue(Object o, String name, String value) {
        try {
            InvocationHandler h= Proxy.getInvocationHandler(o);
            Field hField=h.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            Map memberValues=(Map)hField.get(h);
            memberValues.put(name,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<OperMsg> eq(Object object1, Object object2) {
        List<OperMsg> operMsgList = new ArrayList<>();
        try {
            Field[] fields = object1.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();
                if ("serialVersionUID".equals(name)) {
                    continue;
                }
                TableField exist = field.getAnnotation(TableField.class);
                if (exist != null && !exist.exist()) {
                    continue;
                }
                if (exist != null && exist.fill() != null) {
                    continue;
                }
                ApiModelProperty attr = field.getAnnotation(ApiModelProperty.class);
                String alias = "";
                if (attr != null && (StrUtil.isNotEmpty(attr.value()))) {
                    alias = attr.value();
                }
                String Name = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m = object1.getClass().getMethod("get" + Name);
                Object value1 = m.invoke(object1);
                Object value2 = m.invoke(object2);
                if (value1 instanceof Date) {
                    value1= DateUtil.format((Date) value1, "yyyy-MM-dd HH:mm:ss");
                    value2= DateUtil.format((Date) value2, "yyyy-MM-dd HH:mm:ss");
                }
                compareValue(operMsgList, alias, name, value1, value2);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return operMsgList;
    }

    public static List<OperMsg> setDiff(Object object, String fieldName, Object value1, Object value2, List<OperMsg> operMsgList) {
        if (fieldName != null && !"".equals(fieldName) && isNotEquals(value1, value2)) {
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();
                if (!name.equals(fieldName)) {
                    continue;
                }
                TableField exist = field.getAnnotation(TableField.class);
                if (exist != null && !exist.exist()) {
                    continue;
                }
                if (exist != null && exist.fill() != null) {
                    continue;
                }
                ApiModelProperty attr = field.getAnnotation(ApiModelProperty.class);
                String alias = "";
                if (attr != null && (StrUtil.isNotEmpty(attr.value()))) {
                    alias = attr.value();
                }
                compareValue(operMsgList, alias, name, value1, value2);
            }
        }
        return operMsgList;
    }

    private static boolean isNotEquals(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return false;
        }
        if (value1 == null) {
            value1 = "";
        }
        if (value2 == null) {
            value2 = "";
        }
        return !value1.equals(value2);
    }

    private static void compareValue(List<OperMsg> operMsgList, String alias, String name, Object value1, Object value2) {
        if (isNotEquals(value1, value2)) {
            OperMsg msg = new OperMsg(name, alias, value1, value2);
            operMsgList.add(msg);
        }
    }

}
