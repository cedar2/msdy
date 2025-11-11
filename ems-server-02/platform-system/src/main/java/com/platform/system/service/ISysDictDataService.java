package com.platform.system.service;

import java.util.List;
import java.util.Map;

import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.domain.model.DictData;

/**
 * 字典 业务层
 *
 * @author platform
 */
public interface ISysDictDataService
{
    /**
     * 查询所有的字典列表
     * @return
     */
    Map<String,Object> getDictDataList();

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    SysDictData selectDictDataById(Long dictCode);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    int deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    int updateDictData(SysDictData dictData);

    int checkData(Long[] dictCodes);

    /**
     * 根据字典类型查找字典数据
     * @param dictType
     * @return
     */
    List<DictData> selectDictData(String dictType);
    /**
     * 根据字典类型清除缓存
     * @param dictType
     * @return
     */
    public boolean deleteDictData(String dictType);



}
