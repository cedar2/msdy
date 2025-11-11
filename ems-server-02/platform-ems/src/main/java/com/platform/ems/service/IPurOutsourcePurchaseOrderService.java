package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourcePurchaseOrder;

/**
 * 外发加工单Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IPurOutsourcePurchaseOrderService extends IService<PurOutsourcePurchaseOrder>{
    /**
     * 查询外发加工单
     * 
     * @param outsourcePurchaseOrderSid 外发加工单ID
     * @return 外发加工单
     */
    public PurOutsourcePurchaseOrder selectPurOutsourcePurchaseOrderById(Long outsourcePurchaseOrderSid);

    /**
     * 查询外发加工单列表
     * 
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 外发加工单集合
     */
    public List<PurOutsourcePurchaseOrder> selectPurOutsourcePurchaseOrderList(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

    /**
     * 新增外发加工单
     * 
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    public int insertPurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

    /**
     * 修改外发加工单
     * 
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    public int updatePurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

    /**
     * 变更外发加工单
     *
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    public int changePurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

    /**
     * 批量删除外发加工单
     * 
     * @param outsourcePurchaseOrderSids 需要删除的外发加工单ID
     * @return 结果
     */
    public int deletePurOutsourcePurchaseOrderByIds(List<Long> outsourcePurchaseOrderSids);

    /**
     * 更改确认状态
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return
     */
    int check(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

}
