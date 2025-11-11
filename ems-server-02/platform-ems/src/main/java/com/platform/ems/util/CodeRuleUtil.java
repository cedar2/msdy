package com.platform.ems.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;
import com.platform.ems.plug.service.IConDataobjectCodeRuleService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 编码规则 配置信息
 *
 * @author chenkw
 */
public class CodeRuleUtil {

    @Autowired
    private static IConDataobjectCodeRuleService conDataobjectCodeRuleService;

    @Autowired
    private static RedissonClient redissonClient;

    @Autowired
    private static RedisCache redisService;

    private static ExecutorService executor = ThreadUtil.newExecutor(5);

    private static String prefix = "prefix";

    private static String serial = "serial";

    private static String max = "max";

    private static void init() {
        conDataobjectCodeRuleService = SpringUtil.getBean(IConDataobjectCodeRuleService.class);
        redissonClient = SpringUtil.getBean(RedissonClient.class);
        redisService = SpringUtil.getBean(RedisCache.class);
    }

    /**
     * 得到当前编码值（用于正常单笔新建）
     *
     * @param dataObjectCode   数据对象类别
     * @param businessCategory 业务类型：颜色/尺码：YS/CM
     */
    public static synchronized Map<String,String> allocation(String dataObjectCode, String businessCategory) {
        String clientId = SecurityUtils.getClientId();
        if (StrUtil.isEmpty(clientId)) {
            throw new CustomException("当前用户不允许操作业务");
        }
        if (StrUtil.isBlank(businessCategory)){
            businessCategory = "";
        }
        init();
        String lockKey = dataObjectCode + businessCategory + "GetCode:" + clientId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10L, TimeUnit.SECONDS);
        //返回的完整编码 + redis的key
        Map<String, String> response = new HashMap<>();
        String code = "";
        try {
            String code_cache_key = dataObjectCode + businessCategory + "Code:" + clientId;
            //redis读出来的数据
            Map<String, String> map = redisService.getCacheObject(code_cache_key);
            //根据参数初始化
            ConDataobjectCodeRule codeRule = initialize(dataObjectCode, businessCategory);
            //没有缓存
            if (map == null) {
                //查询表记录
                codeRule.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
                codeRule = serialNumberCurrent(codeRule);
                //找不到编码规则或者不是系统自动编码就返回
                if (codeRule == null || !ConstantsEms.CODE_MODE_ZD.equals(codeRule.getCodeMode())) {
                    return null;
                }
                //默认新增一笔记录
                codeRule.setNumber(1L);
                //有前缀
                if (StrUtil.isNotBlank(codeRule.getPrefix())) {
                    code = codeRule.getPrefix() + codeRule.getSerialNumberCurrent();
                }
                //无前缀
                else {
                    code = codeRule.getSerialNumberCurrent().toString();
                }
                //更新编码
                codeRule.setSerialNumberCurrent(codeRule.getSerialNumberCurrent() + 1);
                addCurrentNumber(codeRule, code_cache_key);
            }
            //有缓存
            else {
                code = map.get(prefix) + map.get(serial);
                //更新编码
                codeRule.setSerialNumberTo(Long.parseLong(map.get(max)));
                codeRule.setPrefix(map.get(prefix));
                //默认新增一笔记录
                codeRule.setSerialNumberCurrent(Long.parseLong(map.get(serial)) + 1);
                addCurrentNumber(codeRule, code_cache_key);
            }
            //返回 系统编码 和 缓存的Key目的是为了来源报错时清空缓存，不然缓存的编码会变为加1后的数据
            response.put(AutoIdField.code,code);
            response.put(AutoIdField.key_name,code_cache_key);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        } finally {
            lock.unlock();
        }
        return response;
    }

    /**
     * 得到当前编码值（用于导入等多笔新建）
     *
     * @param dataObjectCode   数据对象类别
     * @param businessCategory 业务类型：颜色/尺码：YS/CM
     * @return key：前缀，value：初始编码段
     */
    public static Map<String, String> allocations(String dataObjectCode, String businessCategory, Long number) {
        String clientId = SecurityUtils.getClientId();
        if (StrUtil.isEmpty(clientId)) {
            throw new BaseException("当前用户不允许操作业务");
        }
        if (StrUtil.isBlank(businessCategory)){
            businessCategory = "";
        }
        init();
        String lockKey = dataObjectCode + businessCategory + "GetCode:" + clientId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10L, TimeUnit.SECONDS);
        Map<String, String> response = new HashMap<>();
        try {
            String code_cache_key = dataObjectCode + businessCategory + "Code:" + clientId;
            Map<String, String> map = redisService.getCacheObject(code_cache_key);
            //根据参数初始化
            ConDataobjectCodeRule codeRule = initialize(dataObjectCode, businessCategory);
            //没有缓存
            if (map == null){
                codeRule.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
                codeRule = serialNumberCurrent(codeRule);
                //不是系统编码就返回
                if (codeRule == null || !ConstantsEms.CODE_MODE_ZD.equals(codeRule.getCodeMode())) {
                    redisService.deleteObject(code_cache_key);
                    return null;
                }
                //前缀
                if (StrUtil.isNotBlank(codeRule.getPrefix())) {
                    response.put(prefix,codeRule.getPrefix());
                }else {
                    response.put(prefix,"");
                }
                response.put(serial, codeRule.getSerialNumberCurrent().toString());
                //更新编码
                codeRule.setNumber(number);
                if (codeRule.getSerialNumberCurrent().intValue() + number.intValue() == codeRule.getSerialNumberTo().intValue() + 1) {
                    codeRule.setStatus(ConstantsEms.DISENABLE_STATUS);
                }
                //更新编码
                codeRule.setSerialNumberCurrent(codeRule.getSerialNumberCurrent() + number.intValue());
                addCurrentNumber(codeRule, code_cache_key);
            }
            //有缓存
            else {
                //更新编码
                codeRule.setSerialNumberTo(Long.parseLong(map.get(max)));
                codeRule.setPrefix(map.get(prefix));
                //默认新增一笔记录
                codeRule.setSerialNumberCurrent(Long.parseLong(map.get(serial)) + number);
                addCurrentNumber(codeRule, code_cache_key);
            }
            response.put(AutoIdField.key_name,code_cache_key);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        } finally {
            lock.unlock();
        }
        return response;
    }


    /**
     * 当前编码段值更新
     *
     * @param codeRule ConDataobjectCodeRule
     */
    public synchronized static void addCurrentNumber(ConDataobjectCodeRule codeRule, String code_cache_key) {
        //如果编码段值大于设置的最大编码段则提示
        if (codeRule.getSerialNumberCurrent().intValue() > codeRule.getSerialNumberTo().intValue() + 1) {
            if (redisService.getCacheObject(code_cache_key) != null){
                redisService.deleteObject(code_cache_key);
            }
            throw new CustomException("配置的编码段已超出，请重新分配编码段");
        }
        if (codeRule.getSerialNumberCurrent().intValue() == codeRule.getSerialNumberTo().intValue() + 1) {
            codeRule.setStatus(ConstantsEms.DISENABLE_STATUS);
        }
        String uuid = UUID.randomUUID().toString();
        HashMap<String, Object> json = new HashMap<>();
        json.put(prefix, "");
        if (StrUtil.isNotBlank(codeRule.getPrefix())) {
            json.put(prefix, codeRule.getPrefix());
        }
        json.put(max, codeRule.getSerialNumberTo().toString());
        conDataobjectCodeRuleService.addCurrentNumber(codeRule);
        json.put(serial, codeRule.getSerialNumberCurrent().toString());
        String key = AutoIdField.code_key + ":" + uuid;
        redisService.setCacheObject(key, uuid, 10L, TimeUnit.MINUTES);
        redisService.setCacheObject(code_cache_key, json);

    }

    /**
     * 得到当前编码段值
     *
     * @param codeRule serialNumberCurrent
     * @return
     */
    public static ConDataobjectCodeRule serialNumberCurrent(ConDataobjectCodeRule codeRule) {
        return conDataobjectCodeRuleService.selectCurrentNumberByRule(codeRule);
    }

    /**
     * 初始化
     *
     * @param dataObjectCode   String
     * @param businessCategory String
     * @return
     */
    public static ConDataobjectCodeRule initialize(String dataObjectCode, String businessCategory) {
        ConDataobjectCodeRule codeRule = new ConDataobjectCodeRule();
        codeRule.setDataobjectCategoryCode(dataObjectCode);
        codeRule.setBusinessCategory(businessCategory);
        return codeRule;
    }
}
