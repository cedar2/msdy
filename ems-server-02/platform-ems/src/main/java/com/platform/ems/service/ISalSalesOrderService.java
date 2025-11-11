package com.platform.ems.service;

import java.util.List;

import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.BasMaterialImResponse;
import com.platform.ems.domain.dto.response.OrderItemStatusRequest;
import com.platform.ems.domain.dto.response.form.SalSaleOrderProcessTracking;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;

/**
 * 销售订单Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface ISalSalesOrderService extends IService<SalSalesOrder> {
    /**
     * 查询销售订单
     *
     * @param salesOrderSid 销售订单ID
     * @return 销售订单
     */
    public SalSalesOrder selectSalSalesOrderById(Long salesOrderSid);

    /**
     * 明细汇总页签
     */
    public SalSalesOrder getItemTotalList(SalSalesOrder salSalesOrder, List<SalSalesOrderItem> items);

    /**
     * 撤回保存前的校验
     *
     * @param salesOrder 订单
     * @return 结果
     */
    public int backSaveVerify(SalSalesOrder salesOrder);

    /**
     * 撤回保存
     *
     * @param salesOrder 订单
     * @return 结果
     */
    public int backSave(SalSalesOrder salesOrder);

    /**
     * 维护物流信息
     *
     * @param salesOrder 订单
     * @return 结果
     */
    public int setCarrier(SalSalesOrder salesOrder);

    /**
     * 维护纸质合同号
     *
     * @param salesOrder 订单
     * @return 结果
     */
    public EmsResultEntity setPaperContract(SalSalesOrder salesOrder);

    /**
     *  按照“商品/物料编码+SKU1序号+SKU1名称+SKU2序号+SKU2名称”升序排列
     * （SKU1序号、SKU2序号，取对应商品/物料档案的“SKU1”、“SKU2”页签中的“序号”清单列的值）
     */
    public List<SalSalesOrderItem> newSort(List<SalSalesOrderItem> itemList);

    //变更驳回清空字段
    public void setValueNull(Long salesOrderSid);

    /**
     * 查询销售订单列表
     *
     * @param salSalesOrder 销售订单
     * @return 销售订单集合
     */
    public List<SalSalesOrder> selectSalSalesOrderList(SalSalesOrder salSalesOrder);
    /**
     * 计算金额
     *
     */
    public  SalSalesOrder getCount(List<SalSalesOrderItem> salSalesOrderItems);
    /**
     * 新增销售订单
     *
     * @param salSalesOrder 销售订单
     * @return 结果
     */
    public int insertSalSalesOrder(SalSalesOrder salSalesOrder);
    public AjaxResult checkListFree(List<Long> sids);
    //销售订单作废校验
    public int disuseJudge(List<Long> sids);

    //销售订单作废
    public int disuse(OrderInvalidRequest request);

    // 明细作废
    public int itemDisuse(OrderInvalidRequest request);

    public int judgeReceipt(SalSalesOrder o);
    /**
     * 拷贝销售订单
     *
     */
    public SalSalesOrder copy(Long salesOrderSid);
    /**
     * 修改面辅料状态、甲供料状态
     *
     */
    public int changeStatus(OrderItemStatusRequest order);
    /**
     * 录入合同号
     */
    public int setConstract(SalSalesOrder order);

    /**
     * 变更合同号
     */
    public int setConstractCode(SalSalesOrder order);

    /**
     * 提交时校验
     */
    public int processCheck(Long salesOrderSid);

    /**
    //多笔提交
     */
    public EmsResultEntity checkList(OrderErrRequest request);

    //校验是否超过合同金额
    public int judgeConstract(SalSalesOrder order);
    /**
     * 创建合同号
     */
    public int addConstract(List<Long> salesOrderSids);

    /**
     * 创建合同
     */
    public EmsResultEntity constractAdd(List<SalSalesOrder> list, String jump);

    /**
     * 修改销售订单
     *
     * @param salSalesOrder 销售订单
     * @return 结果
     */
    public int updateSalSalesOrder(SalSalesOrder salSalesOrder);

    /**
     * 批量删除销售订单
     *
     * @param salesOrderSids 需要删除的销售订单ID
     * @return 结果
     */
    public int deleteSalSalesOrderByIds(Long[] salesOrderSids);
    public InvInventoryDocument hadnleItem(TecBomItemReportRequest request);
    /**
     * 销售订单确认
     */
    int confirm(SalSalesOrder salSalesOrder);
    //设置负责生产工厂
    public int updateOrderProducePlant(OrderProducePlantRequest orderProducePlantRequest);
    /**
     * 销售订单变更
     */
    int change(SalSalesOrder salSalesOrder);

    List<BasMaterial> getMaterialInfo(BasSaleOrderRequest basSaleOrderRequest);

    //更新价格
    public AjaxResult updatePrice(BasSaleOrderRequest basSaleOrderRequest);
    //设置首缸
    public int setShouGang(SalSalesOrderItemSetRequest quest);
    //设置下单状态
    public int setMaterialOrder(MaterialOrderRequest quest);
    //设置到期天数
    public int setToexpireDays(OrderItemToexpireRequest quest);
    /**
     * 物料需求报表(商品销售订单)
     */
    SalSalesOrder getMaterialRequireListByCode(Long salesOrderCode);
    /**
     * 设置签收状态
     *
     */
    public int setSignStatus(OrderItemStatusSignRequest request);
    /**
     * 设置首批
     *
     */
    public int setShouPi(OrderItemShouPiRequest request);

    /**
     * 物料需求报表(商品销售订单)
     */
    List<TecBomItem> getMaterialRequireListByCode2(List<TecBomItem> materialReportFormsRequestLis);

    /**
     * 物料需求报表-拉链(商品销售订单)
     */
    public List<TecBomItem> getMaterialZipper(List<TecBomItem> requestList);

    /**
     * 获取租户默认配置
     */
    public SysDefaultSettingClient getClientSetting();

    /**
     * 商品销售订单 导入
     */
    public AjaxResult importDataSale(MultipartFile file);

    /**
     * 物料销售订单 导入
     */
    public AjaxResult importDataSaleWl(MultipartFile file);

    /**
     * 商品销售退货订单 导入
     */
    public int importDataSaleBACK(MultipartFile file);

    /**
     * 客户寄售结算单 导入
     */
    public int importDataSaleCus(MultipartFile file);

    /**
     * 商品 导入-物料需求测算
     */
    public List<BasMaterialImResponse> importDataMaterial(MultipartFile file);

    /**
     * 关闭
     */
    public int close(SalSalesOrder salSalesOrder);

    /**
     * 明细关闭
     */
    public int itemClose(SalSalesOrderItem salSalesOrderItem);

    //商品销售订单明细导出
    public void exportGood(HttpServletResponse response, Long[] sids);
    //物料销售订单明细导出
    public void exportWl(HttpServletResponse response, Long[] sids);

    //物料需求测算创建销售订单
    public SalSalesOrder getOrder(List<TecBomItemReport> order);

    /**
     * 明细报表更新销售价
     */
    public AjaxResult updatePrice(List<SalSalesOrderItem> salSalesOrderItemList);

    /**
     * 设置委托人
     */
    public int setTrustor(OrderTrustorAccountRequest order);

    /**
     * 新建直接点提交/编辑点提交
     *
     * @param salSalesOrder 销售订单
     * @param jump 是否忽略并继续 提示校验  N 第一次校验 ， Y 忽略，
     * @return 结果
     */
    AjaxResult submit(SalSalesOrder salSalesOrder, String jump);

    /**
     * 销售出库进度跟踪报表
     *
     * @param salSaleOrder 销售订单
     * @return 销售订单集合
     */
    public List<SalSaleOrderProcessTracking> selectSalSaleProcessTrackingList(SalSaleOrderProcessTracking salSaleOrder);

}
