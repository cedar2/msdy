package com.platform.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.core.domain.entity.SysDictTypeTemplate;

/**
 * 字典表 数据层
 *
 * @author platform
 */
public interface SysDictTypeMapper extends BaseMapper<SysDictType>
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
     * 通过字典ID删除字典信息
     *
     * @param dictId 字典ID
     * @return 结果
     */
    public int deleteDictTypeById(Long dictId);

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int updateDictType(SysDictType dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    public SysDictType checkDictTypeUnique(SysDictType dict);

    /**
     * 校验字典名称是否唯一
     *
     * @param dict 字典名称
     * @return 结果
     */
    public SysDictType checkDictNameUnique(SysDictType dict);

    /**
     * 验证是否存在这个字典类型
     *
     * @author chenkaiwen 2021/01/21
     * @param dictType 字典类型
     * @return 结果
     */
    public SysDictTypeTemplate selectDictByDictType(String dictType);

    /**
     * 新增数据字典类型信息
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

    public int checkDictType(SysDictType dict);
}
