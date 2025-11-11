package com.platform.ems.service;

import java.util.List;
import com.platform.ems.domain.TecModelPosSize;

/**
 * 版型-部位-尺码-尺寸Service接口
 * 
 * @author linhongwei
 * @date 2021-02-08
 */
public interface ITecModelPosSizeService {
    /**
     * 查询版型-部位-尺码-尺寸
     * 
     * @param clientId 版型-部位-尺码-尺寸ID
     * @return 版型-部位-尺码-尺寸
     */
    public TecModelPosSize selectTecModelPosSizeById(String clientId);

    /**
     * 查询版型-部位-尺码-尺寸列表
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 版型-部位-尺码-尺寸集合
     */
    public List<TecModelPosSize> selectTecModelPosSizeList(TecModelPosSize tecModelPosSize);

    /**
     * 新增版型-部位-尺码-尺寸
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 结果
     */
    public int insertTecModelPosSize(TecModelPosSize tecModelPosSize);

    /**
     * 修改版型-部位-尺码-尺寸
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 结果
     */
    public int updateTecModelPosSize(TecModelPosSize tecModelPosSize);

    /**
     * 批量删除版型-部位-尺码-尺寸
     * 
     * @param clientIds 需要删除的版型-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecModelPosSizeByIds(String[] clientIds);

    /**
     * 删除版型-部位-尺码-尺寸信息
     * 
     * @param clientId 版型-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecModelPosSizeById(String clientId);
}
