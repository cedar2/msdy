package com.platform.ems.service;

import java.util.List;

import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.OrderItemStatusRequest;
import com.platform.ems.domain.dto.response.PurPurchaseOrderOutResponse;
import com.platform.ems.domain.dto.response.PurchaseOrderProgressItemResponse;
import com.platform.ems.domain.dto.response.PurchaseOrderProgressResponse;
import com.platform.ems.domain.dto.response.form.PurPurchaseOrderProcessTracking;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;

/**
 * 采购订单Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IPurPurchaseOrderService extends IService<PurPurchaseOrder>{
    /**
     * 查询采购订单
     * @param purchaseOrderSid 采购订单ID
     * @return 采购订单
     */
    public PurPurchaseOrder selectPurPurchaseOrderById(Long purchaseOrderSid);

    /**
     * 明细汇总页签
     */
    public PurPurchaseOrder getItemTotalList(PurPurchaseOrder purchaseOrder, List<PurPurchaseOrderItem> items);

    /**
     * 明细汇总页签
     */
    public PurPurchaseOrder getItemTotalListWl(PurPurchaseOrder purchaseOrder, List<PurPurchaseOrderItem> items);

    /**
     *外部系统获取采购订单
     *
     * @param purchaseOrderSid 采购订单ID
     * @return 采购订单
     */
    public PurPurchaseOrderOutResponse getOutOrder(Long purchaseOrderSid);

    /**
     * 明细排序规则
     *
     * @param purPurchaseOrderItemList 采购订单明细
     * @return 采购订单
     */
    public List<PurPurchaseOrderItem> newSort(List<PurPurchaseOrderItem> purPurchaseOrderItemList);

    /**
     * 拷贝采购订单
     *
     */
    public PurPurchaseOrder copy(Long purchaseOrderSid);
    //面辅料状态、客供料状态
    public int changeStatus(OrderItemStatusRequest order);
    /**
     * 关闭
     */
    public int close(PurPurchaseOrder purPurchaseOrder);

    /**
     * 明细关闭
     */
    public int itemClose(PurPurchaseOrderItem purPurchaseOrderItem);

    /**
     * 查询采购订单列表
     *
     * @param purPurchaseOrder 采购订单
     * @return 采购订单集合
     */
    public List<PurPurchaseOrder> selectPurPurchaseOrderList(PurPurchaseOrder purPurchaseOrder);
    /**
     * 采购订单计算值刷新
     */
    public PurPurchaseOrder getCount(List<PurPurchaseOrderItem> purPurchaseOrderItems);
    /**
     * 录入采购合同
     */
    public int setConstract(PurPurchaseOrder order);

    /**
     * 变更合同号
     */
    public int setConstractCode(PurPurchaseOrder order);

    /**
     * 生成二维码
     */
    public List<PurPurchaseOrderItem> getQr(List<PurPurchaseOrderItem> list);

    /**
     * 创建合同
     */
    public EmsResultEntity constractAdd(List<PurPurchaseOrder> list, String jump);

    /**
     * 新增采购订单
     *
     * @param purPurchaseOrder 采购订单
     * @return 结果
     */
    public int insertPurPurchaseOrder(PurPurchaseOrder purPurchaseOrder);

    /**
     * 新建直接点提交/编辑点提交
     *
     * @param purPurchaseOrder 采购订单
     * @param jump 是否忽略并继续 提示校验  N 第一次校验 ， Y 忽略，
     * @return 结果
     */
    AjaxResult submit(PurPurchaseOrder purPurchaseOrder, String jump);

    /**
     * 撤回保存前的校验
     *
     * @param purPurchaseOrder 订单
     * @return 结果
     */
    public int backSaveVerify(PurPurchaseOrder purPurchaseOrder);

    /**
     * 撤回保存
     *
     * @param purPurchaseOrder 订单
     * @return 结果
     */
    public int backSave(PurPurchaseOrder purPurchaseOrder);

    /**
     * 维护纸质合同号
     *
     * @param purPurchaseOrder 订单
     * @return 结果
     */
    public EmsResultEntity setPaperContract(PurPurchaseOrder purPurchaseOrder);

    /**
     * 获取租户默认配置
     */
    public SysDefaultSettingClient getClientSetting();

    /**
     * 采购订单
     *
     * @param
     * @return 结果
     */
    public PurPurchaseOrder getOrder(List<TecBomItemReport> order);
    /**
     * 外部系统接口-修改采购订单状态
     */
    public int changeHandleOut(List<PurPurchaseOrderHandleRequest> list);
    //采购订单作废
    public int disusejudge(List<Long> sids);

    //采购订单作废
    public int disuse(OrderInvalidRequest request);

    // 明细作废
    public int itemDisuse(OrderInvalidRequest request);

    /**
     * 修改采购订单
     *
     * @param purPurchaseOrder 采购订单
     * @return 结果
     */
    public int updatePurPurchaseOrder(PurPurchaseOrder purPurchaseOrder);
    /**
     * 设置委托人
     */
    public int setTrustor(OrderTrustorAccountRequest order);
    /**
     * 批量删除采购订单
     *
     * @param purchaseOrderSids 需要删除的采购订单ID
     * @return 结果
     */
    public int deletePurPurchaseOrderByIds(Long[] purchaseOrderSids);

    /**
     * 采购订单驳回
     */
    public int returnSourceNewQuantity(PurPurchaseOrder purPurchaseOrder);

    /**
     * 采购订单确认
     */
    int confirm(PurPurchaseOrder purPurchaseOrder);

    /**
     * 采购订单变更
     */
    int change(PurPurchaseOrder purPurchaseOrder);

    //设置到期天数
    public int setToexpireDays(OrderItemToexpireRequest quest);
    /**
     * 提交时校验
     */
    public int checkProcess(Long purPurchaseOrderSid);

    /**
     * 多笔提交时校验
     */
    public EmsResultEntity checkProcessList(OrderErrRequest request);

    //明细报表跳转入库
    public InvInventoryDocument exChange(List<PurPurchaseOrderItem> items);
    //判断核销状态
    public int judgeReceipt(PurPurchaseOrder purPurchaseOrder);
    //多笔提交
    public AjaxResult checkListFree(List<Long> sids);
    /**
     * 物料需求报表(商品采购订单)
     */
    PurPurchaseOrder getMaterialRequireListByCode(Long purchaseOrderCode);

    /**
     * 物料采购订单 导入
     */
    public AjaxResult importDataM(MultipartFile file);

    /**
     * 物料采购退货订单 导入
     */
    public int importDataRe(MultipartFile file);

    /**
     * 商品采购订单 导入
     */
    public AjaxResult importDataG(MultipartFile file);

    /**
     * 商品采购退货订单 导入
     */
    public int importDataGre(MultipartFile file);

    /**
     * 供应商寄售结算单 导入
     */
    public int importDataVe(MultipartFile file);

    //物料 采购订单明细导出
    public void export(HttpServletResponse response, Long[] sids);
    //商品 采购订单明细导出
    public void exportGood(HttpServletResponse response, Long[] sids);
    void inventoryDocument(PurPurchaseOrder o);
    void advancesReceived(PurPurchaseOrder o);
    //校验是否超过合同金额
    public int judgeConstract(PurPurchaseOrder order);
    public void setValueNull(Long sid);
    /**
     * 设置签收状态
     *
     */
    public int setSignStatus(OrderItemStatusSignRequest request);

    /**
     * 采购订单报表更新采购价
     */
    public AjaxResult updatePrice(List<PurPurchaseOrderItem> purPurchaseOrderItems);

    //采购进度报表主
    public List<PurchaseOrderProgressResponse> getProcessHead(PurchaseOrderProgressRequest request);

    //采购进度报表-明细
    public List<PurchaseOrderProgressItemResponse> getProcessItem(PurchaseOrderProgressRequest request);

    /**
     * 采购入库进度跟踪报表
     *
     * @param purPurchaseOrder 采购订单
     * @return 采购订单集合
     */
    public List<PurPurchaseOrderProcessTracking> selectPurPurchaseProcessTrackingList(PurPurchaseOrderProcessTracking purPurchaseOrder);

    void exportHetongList(HttpServletResponse response, PurPurchaseOrder purPurchaseOrder);
}
