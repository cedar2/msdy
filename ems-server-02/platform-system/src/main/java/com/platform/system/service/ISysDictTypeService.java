package com.platform.system.service;

import java.util.List;
import java.util.Map;

import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.core.domain.entity.SysDictTypeTemplate;

/**
 * 字典 业务层
 *
 * @author platform
 */
public interface ISysDictTypeService
{
    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeAll();

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    public List<SysDictData> selectDictDataByType(String dictType,String clientId);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    public SysDictType selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    public SysDictType selectDictTypeByType(String dictType);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    public Map<String,Object> selectDictDataByTypeList(SysDictType dictType, String clientId);

    /**
     * 批量删除字典信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 清空缓存数据
     */
    public void clearCache();

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int updateDictType(SysDictType dictType);


    public int check(SysDictType dictType);


    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    public String checkDictTypeUnique(SysDictType dictType);

    /**
     * 校验字典名称是否唯一
     *
     * @param dictType 字典名称
     * @return 结果
     */
    public String checkDictNameUnique(SysDictType dictType);

    /**
     * 导入字典类型
     *
     * @author chenkaiwen 2021/01/21
     * @param dictList        字典类型列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    public String importDict(List<SysDictTypeTemplate> dictList, Boolean isUpdateSupport, String operName);

    /**
     * 新增字典类型信息
     *
     * @author chenkaiwen 2021/01/21
     * @param dict 字典类型
     * @return 结果
     */
    public int insertDict(SysDictTypeTemplate dict);

    /**
     * 修改字典类型
     *
     * @author chenkaiwen 2021/01/21
     * @param dict 字典类型
     * @return 结果
     */
    public int updateDict(SysDictTypeTemplate dict);
}
