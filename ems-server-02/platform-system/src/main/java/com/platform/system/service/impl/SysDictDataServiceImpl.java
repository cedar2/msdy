package com.platform.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.system.mapper.SysDictTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.utils.DictUtils;
import com.platform.system.mapper.SysDictDataMapper;
import com.platform.system.service.ISysDictDataService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 字典 业务层处理
 *
 * @author platform
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Resource
    private SysDictDataMapper dictDataMapper;
    @Resource
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private RedisCache redisService;

    private final static String LEVEL_XT = "XT";

    private final static String LEVEL_YH = "YH";

    @Override
    @Cacheable(value = "dict_data#3600", keyGenerator = "myKeyGenerator")
    public Map<String, Object> getDictDataList() {
        LoginUser user = ApiThreadLocalUtil.get();
        Map<String, Object> dataMap = new HashMap<>();
        List<DictData> dataList = dictDataMapper.getDictDataList();
        if (user.getUserid() != null && 1L == user.getUserid()) {
            for (DictData data : dataList) {
                List<DictData> valueList = data.getDataList();
                String keyName = data.getDictType();
                dataMap.put(keyName, valueList);
            }
        } else {
            List<DictData> privateList = dictDataMapper.getDictDataPrtvateList(user.getSysUser().getClientId());
            if (privateList != null && privateList.size() > 0) {
                dataList.removeAll(privateList);
                dataList.addAll(privateList);
            }
            for (DictData data : dataList) {
                List<DictData> valueList = data.getDataList();
                String keyName = data.getDictType();
                dataMap.put(keyName, valueList);
            }
        }
        return dataMap;
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getSysUser();
        List<SysDictData> sysDictData = new ArrayList<>();
        if (user != null) {
            if (user.getUserId() != null && user.getUserId().equals(1L)) {
                sysDictData = dictDataMapper.selectDictDataList(dictData);
            } else {
                List<String> clientList = new ArrayList<>();
                clientList.add(user.getClientId());
                if (!LEVEL_YH.equals(dictData.getDictLevel())) {
                    clientList.add(UserConstants.ADMIN_CLIENTID);
                }
                dictData.setClientIdList(clientList.toArray(new String[clientList.size()]));
                List<SysDictData> privateList = dictDataMapper.selectDictDataList(dictData);
                if (ArrayUtil.isNotEmpty(privateList)) {
                    sysDictData.addAll(privateList);
                }
            }
        }
        return sysDictData;
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictDataByIds(Long[] dictCodes) {
        SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
        String clientId = sysUser.getClientId();
        for (Long code : dictCodes) {
            SysDictData data = dictDataMapper.selectDictDataById(code);
            if (data.getHandleStatus().equals("5")) {
                throw new CustomException(data.getDictLabel() + "状态已确认,不允许修改");
            }
            if (sysUser.getUserId() != null && !sysUser.getUserId().equals(1L)) {
                if (!data.getClientId().equals(clientId)) {
                    throw new CustomException("只允许删除自定义字典");
                }
            }
        }
        int row = dictDataMapper.deleteDictDataByIds(dictCodes);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        SysDictData params = new SysDictData();
        SysDictType dictType = dictTypeMapper.selectDictTypeByType(dictData.getDictType());
        List<String> clientList = new ArrayList<>();
        if (dictType != null && LEVEL_YH.equals(dictType.getDictLevel())) {
            dictData.setClientId(ApiThreadLocalUtil.get().getClientId());
//			clientList.add(UserConstants.ADMIN_CLIENTID);
            clientList.add(dictData.getClientId());
            params.setClientIdList(clientList.toArray(new String[clientList.size()]));
        }

        //校验是否重复
        params.setDictLabel(dictData.getDictLabel());
        params.setDictType(dictData.getDictType());
//		List<SysDictData> queryResult=dictDataMapper.selectDictDataList(params);
        List<SysDictData> queryResult = dictDataMapper.verify(params);
        if (CollUtil.isNotEmpty(queryResult)) {
            throw new BaseException("数据标签已存在");
        }
        params.setDictLabel(null);
        params.setDictValue(dictData.getDictValue());
//		queryResult=dictDataMapper.selectDictDataList(params);
        queryResult = dictDataMapper.verify(params);
        if (CollUtil.isNotEmpty(queryResult)) {
            throw new BaseException("数据键值已存在");
        }
        int row = dictDataMapper.insertDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String checkStatus = "5";
        SysDictData oldData = dictDataMapper.selectDictDataById(dictData.getDictCode());
        if (checkStatus.equals(dictData.getHandleStatus())) {
            // 已确认状态不允许修改value
            if (!oldData.getDictValue().equals(dictData.getDictValue())) {
                throw new CustomException("确认状态字典不允许修改");
            }
        }
        SysUser sysUser = loginUser.getSysUser();
        dictData.setUpdateBy(sysUser.getUserName());
        if (sysUser.getUserId() != null && !sysUser.getUserId().equals(1L)) {
            if (!oldData.getClientId().equals(sysUser.getClientId())) {
                throw new CustomException("只允许修改自定义字典");
            }
        }
        //校验标签是否重复
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictLabel(dictData.getDictLabel());
        sysDictData.setDictType(dictData.getDictType());
        //权限范围
        SysDictType dictType = dictTypeMapper.selectDictTypeByType(dictData.getDictType());
        List<String> clientList = new ArrayList<>();
        if (dictType != null && LEVEL_YH.equals(dictType.getDictLevel())) {
            clientList.add(ApiThreadLocalUtil.get().getClientId());
            sysDictData.setClientIdList(clientList.toArray(new String[clientList.size()]));
        }
        List<SysDictData> dictDataList = dictDataMapper.selectDictDataListByJingque(sysDictData);
        if (CollUtil.isNotEmpty(dictDataList)) {
            dictDataList.forEach(o -> {
                if (!dictData.getDictCode().equals(o.getDictCode())) {
                    throw new BaseException("数据标签已存在");
                }
            });
        }
        int row = dictDataMapper.updateDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    @Override
    public int checkData(Long[] dictCodes) {
        return dictDataMapper.checkData(dictCodes);
    }

    @Override
    public List<DictData> selectDictData(String dictType) {
        LoginUser user = ApiThreadLocalUtil.get();
        String key = "dictData:";
        if (user.getUserid() != null && 1L == user.getUserid()) {
            key += "admin";
        } else {
            key += user.getSysUser().getClientId();
        }
        key += ":" + dictType;
        List<DictData> dataList = redisService.getCacheObject(key);
        if (dataList != null && dataList.size() > 0) {
            return dataList;
        }
        dataList = dictDataMapper.selectDictData(dictType);
        //缓存一小时
        redisService.setCacheObject(key, dataList, 1L, TimeUnit.HOURS);
        return dataList;
    }

    @Override
    public boolean deleteDictData(String dictType) {
        LoginUser user = ApiThreadLocalUtil.get();
        String key = "dictData:";
        if (user.getUserid() != null && 1L == user.getUserid()) {
            key += "admin";
        } else {
            key += user.getSysUser().getClientId();
        }
        key += ":" + dictType;
        return redisService.deleteObject(key);
    }
}
