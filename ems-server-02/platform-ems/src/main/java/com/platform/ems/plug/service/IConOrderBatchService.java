package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConOrderBatch;

/**
 * 下单批次Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConOrderBatchService extends IService<ConOrderBatch>{
    /**
     * 查询下单批次
     * 
     * @param sid 下单批次ID
     * @return 下单批次
     */
    public ConOrderBatch selectConOrderBatchById(Long sid);

    /**
     * 查询下单批次列表
     * 
     * @param conOrderBatch 下单批次
     * @return 下单批次集合
     */
    public List<ConOrderBatch> selectConOrderBatchList(ConOrderBatch conOrderBatch);

    /**
     * 新增下单批次
     * 
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    public int insertConOrderBatch(ConOrderBatch conOrderBatch);

    /**
     * 修改下单批次
     * 
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    public int updateConOrderBatch(ConOrderBatch conOrderBatch);

    /**
     * 变更下单批次
     *
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    public int changeConOrderBatch(ConOrderBatch conOrderBatch);

    /**
     * 批量删除下单批次
     * 
     * @param sids 需要删除的下单批次ID
     * @return 结果
     */
    public int deleteConOrderBatchByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conOrderBatch
    * @return
    */
    int changeStatus(ConOrderBatch conOrderBatch);

    /**
     * 更改确认状态
     * @param conOrderBatch
     * @return
     */
    int check(ConOrderBatch conOrderBatch);

    /**
     * 下单批次下拉框
     */
    List<ConOrderBatch> getList();
}
