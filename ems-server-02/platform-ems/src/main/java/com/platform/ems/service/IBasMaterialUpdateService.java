package com.platform.ems.service;

import java.util.HashMap;

/**
 * 根据物料商品更新其它表
 */
public interface IBasMaterialUpdateService {

    /**
     * 修改其它单据同步物料商品档案
     *
     * @param map 请求
     * @return 结果
     */
    int updateFromMaterial(HashMap<String, String> map);
}
