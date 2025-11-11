package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.SalSalesIntentOrder;
import com.platform.ems.domain.SalSalesIntentOrderAttach;
import com.platform.ems.domain.SalSalesIntentOrderItem;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 销售意向单Service接口
 *
 * @author chenkw
 * @date 2022-10-17
 */
public interface ISalSalesIntentOrderService extends IService<SalSalesIntentOrder> {
    /**
     * 查询销售意向单
     *
     * @param salesIntentOrderSid 销售意向单ID
     * @return 销售意向单
     */
    public SalSalesIntentOrder selectSalSalesIntentOrderById(Long salesIntentOrderSid);

    /**
     * 复制销售意向单
     *
     * @param salesIntentOrderSid 销售意向单ID
     * @return 销售意向单
     */
    public SalSalesIntentOrder copySalSalesIntentOrderById(Long salesIntentOrderSid);

    /**
     * 查询销售意向单列表
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 销售意向单集合
     */
    public List<SalSalesIntentOrder> selectSalSalesIntentOrderList(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 新增销售意向单
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    public int insertSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 修改销售意向单
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    public int updateSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 变更销售意向单
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    public int changeSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 批量删除销售意向单
     *
     * @param salesIntentOrderSids 需要删除的销售意向单ID
     * @return 结果
     */
    public int deleteSalSalesIntentOrderByIds(List<Long> salesIntentOrderSids);

    /**
     * 更改确认状态
     *
     * @param salSalesIntentOrder
     * @return
     */
    public int check(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 作废销售意向单 多选 salesIntentOrderSidList
     *
     * @param salSalesIntentOrder
     * @return
     */
    public int cancel(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 设置签收状态
     */
    public int setSignStatus(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 维护纸质合同号
     */
    public EmsResultEntity setPaperContract(SalSalesIntentOrder salesOrder);

    /**
     * 订单明细页签-合计字段刷新
     */
    public SalSalesIntentOrder getCount(List<SalSalesIntentOrderItem> intentOrderItemList);

    /**
     * 查询页面上传附件前的校验
     */
    public AjaxResult checkAttach(SalSalesIntentOrderAttach salesIntentOrderAttach);

    /**
     * 新增附件
     */
    public int insertSalSalesIntentOrderAttach(SalSalesIntentOrderAttach salesIntentOrderAttach);
}
