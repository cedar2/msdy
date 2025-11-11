package com.platform.ems.service;

import com.platform.common.core.domain.model.DictData;

import java.util.List;
import java.util.Map;

/**
 * @author c
 */
public interface ISystemDictDataService {

    /**
     * 查询所有的字典列表
     * @return
     */
    Map<String,Object> getDictDataList();

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
