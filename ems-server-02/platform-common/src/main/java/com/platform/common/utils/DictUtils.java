package com.platform.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.platform.common.constant.CacheConstants;
import com.platform.common.constant.Constants;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.core.experimental.util.MultiTaskLauncher;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.utils.spring.SpringUtils;

/**
 * 字典工具类
 *
 * @author platform
 */
public class DictUtils
{
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 设置字典缓存
     *
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue)
    {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel)
    {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> datas = getDictCache(dictType);

        if (StringUtils.isNotNull(datas))
        {
            if (StringUtils.containsAny(separator, dictValue))
            {
                for (SysDictData dict : datas)
                {
                    for (String value : dictValue.split(separator))
                    {
                        if (value.equals(dict.getDictValue()))
                        {
                            propertyString.append(dict.getDictLabel()).append(separator);
                            break;
                        }
                    }
                }
            }
            else
            {
                for (SysDictData dict : datas)
                {
                    if (dictValue.equals(dict.getDictValue()))
                    {
                        return dict.getDictLabel();
                    }
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> datas = getDictCache(dictType);

        if (StringUtils.containsAny(separator, dictLabel) && StringUtils.isNotEmpty(datas))
        {
            for (SysDictData dict : datas)
            {
                for (String label : dictLabel.split(separator))
                {
                    if (label.equals(dict.getDictLabel()))
                    {
                        propertyString.append(dict.getDictValue()).append(separator);
                        break;
                    }
                }
            }
        }
        else
        {
            for (SysDictData dict : datas)
            {
                if (dictLabel.equals(dict.getDictLabel()))
                {
                    return dict.getDictValue();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key)
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj)) {
            List<SysDictData> dictDatas = StringUtils.cast(cacheObj);
            return dictDatas;
        }
        return null;
    }

    /**
     * 获取字典缓存
     *
     * @param keys 参数键
     * @return dictDatas 字典数据列表
     */
    public static Map<String, Object> getDictCache(String[] keys, String clientId) {
        Map<String, Object> map = new HashMap<>();
        String dictType;
        for (String key : keys) {
            dictType = key;
            key = key + ":" + clientId;
            Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
            if (StringUtils.isNotNull(cacheObj)) {
                List<SysDictData> dictDatas = StringUtils.cast(cacheObj);
                map.put(dictType, dictDatas);
            }
        }
        return map;
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DICT_KEY + "*");
        Collection<String> listKeys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DICT_LIST_KEY + "*");
        keys.addAll(listKeys);
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置字典缓存
     * 使用异步多线程提高效率
     */
    public static void buildDictCache(List<SysDictType> dictTypeList,
                                      Function<SysDictType, List<SysDictData>> selectorOfDictDataByType) {
        RedisCache redisService = SpringUtils.getBean(RedisCache.class);
        // 使用异步多线程，MultiTaskLauncher 封装了异步多线程的代码。
        MultiTaskLauncher.ofDefault().autoGenerateTask(
                // 所有字典类型
                dictTypeList,
                // 对每个字典类型，编写消费逻辑。
                (dictType) -> {
                    // 分别获得 key value
                    String key = getCacheKey(dictType.getDictType());
                    List<SysDictData> value = selectorOfDictDataByType.apply(dictType);
                    // 往 redis 设置缓存
                    redisService.setCacheObject(key, value);
                },
                // 等待所有任务完成。
                true
        );
    }

}
