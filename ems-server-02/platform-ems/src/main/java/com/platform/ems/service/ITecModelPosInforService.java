package com.platform.ems.service;

import java.util.List;
import com.platform.ems.domain.TecModelPosInfor;

/**
 * 版型-部位信息Service接口
 * 
 * @author linhongwei
 * @date 2021-02-08
 */
public interface ITecModelPosInforService {
    /**
     * 查询版型-部位信息
     * 
     * @param clientId 版型-部位信息ID
     * @return 版型-部位信息
     */
    public TecModelPosInfor selectTecModelPosInforById(String clientId);

    /**
     * 查询版型-部位信息列表
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 版型-部位信息集合
     */
    public List<TecModelPosInfor> selectTecModelPosInforList(TecModelPosInfor tecModelPosInfor);

    /**
     * 新增版型-部位信息
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 结果
     */
    public int insertTecModelPosInfor(TecModelPosInfor tecModelPosInfor);

    /**
     * 修改版型-部位信息
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 结果
     */
    public int updateTecModelPosInfor(TecModelPosInfor tecModelPosInfor);

    /**
     * 批量删除版型-部位信息
     * 
     * @param clientIds 需要删除的版型-部位信息ID
     * @return 结果
     */
    public int deleteTecModelPosInforByIds(String[] clientIds);

    /**
     * 删除版型-部位信息信息
     * 
     * @param clientId 版型-部位信息ID
     * @return 结果
     */
    public int deleteTecModelPosInforById(String clientId);
}
