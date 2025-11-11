package com.platform.system.service.impl;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysDictTypeTemplate;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.utils.DictUtils;
import com.platform.common.utils.StringUtils;
import com.platform.system.mapper.SysDictDataMapper;
import com.platform.system.mapper.SysDictTypeMapper;
import com.platform.system.service.ISysDictTypeService;

/**
 * 字典 业务层处理
 *
 * @author platform
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService
{

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Resource
    private SysDictTypeMapper dictTypeMapper;

    @Resource
    private SysDictDataMapper dictDataMapper;

    @Value("${dictCache}")
    private boolean dictCache;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        if (dictCache) {
            log.info("====>开始进行缓存预热");
            DictUtils.buildDictCache(dictTypeMapper.selectDictTypeAll(),
                    dictType -> dictDataMapper.selectDictDataByType(dictType.getDictType()));
        } else {
            log.info("====>缓存预热未开启");
        }
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType, String clientId) {
        String key = dictType + ":" + clientId;
        List<SysDictData> dictDatas = DictUtils.getDictCache(key);
        if (StringUtils.isEmpty(dictDatas)) {
            if (clientId.equals(UserConstants.ADMIN_CLIENTID)) {
                dictDatas = dictDataMapper.selectDictDataByType(dictType);
            } else {
                // 1）若此数据字典的数据等级为“系统”，则仅显示租户ID为10000且启用的数据字典数据
                // 2）若此数据字典的数据等级为“用户”，则仅显示与当前登录账号相同且启用的数据字典数据
                SysDictType sysDictType = dictTypeMapper.selectDictTypeByType(dictType);
                if (sysDictType != null && sysDictType.getDictLevel() != null && "XT".equals(sysDictType.getDictLevel())) {
                    dictDatas = dictDataMapper.selectPublicDictDataByType(dictType);
                }
                else if (sysDictType != null && sysDictType.getDictLevel() != null && "YH".equals(sysDictType.getDictLevel())) {
                    dictDatas = dictDataMapper.selectPrivateDictDataByType(dictType, clientId);
                }
                else {
                    dictDatas = dictDataMapper.selectPublicDictDataByType(dictType);
                    List<SysDictData> privateList = dictDataMapper.selectPrivateDictDataByType(dictType, clientId);
                    if (ArrayUtil.isNotEmpty(privateList)) {
                        dictDatas.addAll(privateList);
                    }
                }
            }
            // 无论是否为空都进行缓存
            DictUtils.setDictCache(key, dictDatas);
        }
        return dictDatas;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    @Override
    public Map<String, Object> selectDictDataByTypeList(SysDictType request, String clientId) {
        Map<String, Object> dictDataMap = new HashMap<>();
        String[] dictTypeList = request.getDictTypeList();
        if (dictTypeList == null || dictTypeList.length == 0) {
            return dictDataMap;
        }
        dictDataMap = DictUtils.getDictCache(dictTypeList, clientId);
        if (MapUtils.isNotEmpty(dictDataMap) && dictDataMap.size() == dictTypeList.length) {
            return dictDataMap;
        }
        List<SysDictData> dictDatas = new ArrayList<>();
        if (clientId.equals(UserConstants.ADMIN_CLIENTID)) {
            for (String dictType : dictTypeList) {
                dictDatas = dictDataMapper.selectDictDataByType(dictType);
                dictDataMap.put(dictType, dictDatas);

                // 无论是否为空都进行缓存
                DictUtils.setDictCache(dictType + ":" + clientId, dictDatas);
            }
        } else {
            for (String dictType : dictTypeList) {
                // 1）若此数据字典的数据等级为“系统”，则仅显示租户ID为10000且启用的数据字典数据
                // 2）若此数据字典的数据等级为“用户”，则仅显示与当前登录账号相同且启用的数据字典数据
                SysDictType sysDictType = dictTypeMapper.selectDictTypeByType(dictType);
                if (sysDictType != null && sysDictType.getDictLevel() != null && "XT".equals(sysDictType.getDictLevel())) {
                    dictDatas = dictDataMapper.selectPublicDictDataByType(dictType);
                }
                else if (sysDictType != null && sysDictType.getDictLevel() != null && "YH".equals(sysDictType.getDictLevel())) {
                    dictDatas = dictDataMapper.selectPrivateDictDataByType(dictType, clientId);
                }
                else {
                    dictDatas = dictDataMapper.selectPublicDictDataByType(dictType);
                    List<SysDictData> privateList = dictDataMapper.selectPrivateDictDataByType(dictType, clientId);
                    if (ArrayUtil.isNotEmpty(privateList)) {
                        dictDatas.addAll(privateList);
                    }
                }
                dictDataMap.put(dictType, dictDatas);

                // 无论是否为空都进行缓存
                DictUtils.setDictCache(dictType + ":" + clientId, dictDatas);
            }
        }
        return dictDataMap;
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(Long[] dictIds) {
        String checkStatus = "5";
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictType.getHandleStatus().equals(checkStatus)) {
                throw new CustomException(String.format("%1$s已确认,不能删除", dictType.getDictName()));
            }
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new CustomException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
        }
        int count = dictTypeMapper.deleteDictTypeByIds(dictIds);
        if (count > 0) {
            DictUtils.clearDictCache();
        }
        return count;
    }

    /**
     * 清空缓存数据
     */
    @Override
    public void clearCache() {
        DictUtils.clearDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dictType) {
        int row = dictTypeMapper.insertDictType(dictType);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dictType) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        String checkStatus = "5";
        if (checkStatus.equals(dictType.getHandleStatus())) {
            // 已确认状态不允许修改字典类型
            if (!dictType.getDictType().equals(oldDict.getDictType())) {
                throw new CustomException("修改字典" + oldDict.getDictName() + "'失败，确认状态不允许修改字典类型");
            }
        }

        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        int row = dictTypeMapper.updateDictType(dictType);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    @Override
    public int check(SysDictType dictType) {
        if (dictType.getDictIds() == null || dictType.getDictIds().length < 1) {
            throw new BaseException("请选择行");
        }
        return dictTypeMapper.checkDictType(dictType);
    }


    /**
     * 校验字典类型是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict);
        if (dictType != null) {
            return UserConstants.NOT_UNIQUE_NUM;
        }
        return UserConstants.UNIQUE_NUM;
    }

    /**
     * 校验字典名称是否唯一
     *
     * @param dict 字典名称
     * @return 结果
     */
    @Override
    public String checkDictNameUnique(SysDictType dict) {
        SysDictType dictType = dictTypeMapper.checkDictNameUnique(dict);
        if (dict.getDictId() == null) {
            if (dictType != null) {
                return UserConstants.NOT_UNIQUE_NUM;
            }
        } else {
            if (dictType != null && !dict.getDictId().equals(dictType.getDictId())) {
                return UserConstants.NOT_UNIQUE_NUM;
            }
        }
        return UserConstants.UNIQUE_NUM;
    }

    /**
     * 导入字典类型数据
     *
     * @param dictList        字典类型数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     * @author chenkaiwen 2021/01/21
     */
    @Override
    public String importDict(List<SysDictTypeTemplate> dictList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(dictList) || dictList.size() == 0) {
            throw new CustomException("导入字典类型数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysDictTypeTemplate dict : dictList) {
            try {
                // 验证是否存在这个字典类型
                SysDictTypeTemplate u = dictTypeMapper.selectDictByDictType(dict.getDictType());
                if (StringUtils.isNull(u)) {
                    dict.setCreateBy(operName);
                    this.insertDict(dict);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、字典类型 " + dict.getDictType() + " 导入成功");
                } else if (isUpdateSupport) {
                    dict.setUpdateBy(operName);
                    this.updateDict(dict);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、字典类型 " + dict.getDictType() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、字典类型 " + dict.getDictType() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、字典类型 " + dict.getDictType() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new CustomException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 新增保存数据字典类型信息
     *
     * @param dict 数据字典类型信息
     * @return 结果
     * @author chenkaiwen 2021/01/21
     */
    @Override
    @Transactional
    public int insertDict(SysDictTypeTemplate dict) {
        // 新增用户信息
        int rows = dictTypeMapper.insertDict(dict);
        return rows;
    }

    /**
     * 修改保存字典信息
     *
     * @param dict 用户信息
     * @return 结果
     * @author chenkaiwen 2021/01/21
     */
    @Override
    @Transactional
    public int updateDict(SysDictTypeTemplate dict) {
        String dictType = dict.getDictType();
        return dictTypeMapper.updateDict(dict);
    }
}
