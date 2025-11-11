package com.platform.ems.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvInventoryDocument;
import com.platform.ems.domain.InvInventoryDocumentItem;
import com.platform.ems.domain.InvStorehouseMaterial;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.DocumentAddItemRequest;
import com.platform.ems.domain.dto.request.InvInventoryDocumentOrders;
import com.platform.ems.domain.dto.request.InvInventoryDocumentReportRequest;
import com.platform.ems.domain.dto.response.InvInventoryDocumentReportResponse;
import com.platform.ems.domain.dto.response.form.InvInventoryProductUserMaterial;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 库存凭证Service接口
 *
 * @author linhongwei
 * @date 2021-04-16
 */
public interface IInvInventoryDocumentService extends IService<InvInventoryDocument>{

    /**
     * 查询出入库明细报表
     *
     * @param invInventoryDocument 库存凭证
     * @return 库存凭证明细报表
     */
    public Map<String, Object> detailReport(InvInventoryDocumentItem invInventoryDocument);

    public List<InvInventoryDocumentItem> filter(InvInventoryDocument invInventoryDocument,String productCodes, String status);

    public List<InvInventoryDocumentItem> sort(List<InvInventoryDocumentItem> items, String type);
    /**
     * 查询库存凭证/甲供料结算单明细报表
     *
     * @param invInventoryDocumentReportRequest 库存凭证
     * @return 库存凭证明细报表
     */
    List<InvInventoryDocumentReportResponse> selectDocumentReport(InvInventoryDocumentReportRequest invInventoryDocumentReportRequest);
    /**
     * 查询库存凭证
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    public InvInventoryDocument selectInvInventoryDocumentById(Long inventoryDocumentSid);

    /**
     * 查询页面更新信息按钮获取信息
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    InvInventoryDocument selectInvInventoryDocumentDetailById(Long inventoryDocumentSid);

    /**
     * 查询页面更新信息按钮修改信息
     *
     * @param invInventoryDocument 库存凭证
     * @return 库存凭证
     */
    int updateInvInventoryDocumentDetailById(InvInventoryDocument invInventoryDocument);

    /**
     *通过业务单号查询对应数据(多订单)
     *
     * @param
     * @return 库存凭证
     */
    public InvInventoryDocument getInvInventoryDocumentList(InvInventoryDocumentOrders invInventoryDocumentOrders);

    /**
     *通过业务单号查询对应数据
     *
     * @param
     * @return 库存凭证
     */
    public InvInventoryDocument getInvInventoryDocument(String code,String referDocCategory,String type,String movementType, String documentCategory, String isMultiOrder);

    /**
     * 进入出入库新建页面获取量
     */
    void getQuantity(InvInventoryDocument invInventoryDocument);

    public List<InvInventoryDocumentItem> getItemAdd(DocumentAddItemRequest request);
    /**
     * 查询库存凭证列表
     *
     * @param invInventoryDocument 库存凭证
     * @return 库存凭证集合
     */
    public List<InvInventoryDocument> selectInvInventoryDocumentList(InvInventoryDocument invInventoryDocument);

    /**
     * 新增库存凭证
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public int insertInvInventoryDocument(InvInventoryDocument invInventoryDocument);

    /**
     * 多作业类型出库
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public int insertInvInventoryDocumentByMovementType(InvInventoryDocument invInventoryDocument);

    /**
     * 打印出库单
     */
    public InvInventoryDocument getPrintck(Long[] sids);
    /**
     * 生成二维码
     */
    public List<InvInventoryDocumentItem> getQr(List<InvInventoryDocumentItem> list);
    public void vatatil(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList);
    /*
     *库存凭证冲销
     */
    public int invDocumentCX(List<Long> sidList);
    public void changeStaus(InvInventoryDocument invInventoryDocument);
    public void createpayment(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList);
    public void createReceipt(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList);
    /**
     * 修改库存凭证
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public int updateInvInventoryDocument(InvInventoryDocument invInventoryDocument);
    //采购结算单导出
    public void exportPur(HttpServletResponse response,List<InvInventoryDocumentReportResponse> list);
   //导出销售结算单
    public void exportSal(HttpServletResponse response,List<InvInventoryDocumentReportResponse> list);
    /**
     * 批量删除库存凭证
     *
     * @param inventoryDocumentSids 需要删除的库存凭证ID
     * @return 结果
     */
    public int deleteInvInventoryDocumentByIds(Long[] inventoryDocumentSids);

    /**
     * 库存凭证确认
     */
    int confirm(Long[] inventoryDocumentSidList);

    /**
     * 库存凭证变更
     */
    int change(InvInventoryDocument invInventoryDocument);
    /**
     * 复制
     */
    public InvInventoryDocument getCopy(Long sid);

    /**
     * 出入库添加明细行时获取价格回传前端
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    List<InvInventoryDocumentItem> setInvInventoryDocumentItemPrice(InvInventoryDocument invInventoryDocument);

    /**
     * 出入库明细获取库存量回传前端
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    List<InvInventoryDocumentItem> getItemUnlimitedQuantity(InvInventoryDocument invInventoryDocument);

    /**
     * 多作业类型出库  按明细获取库存量
     *
     * @param item 库存凭证
     * @return 结果
     */
    List<InvInventoryDocumentItem> getItemUnlimitedQuantityBymovementType(List<InvInventoryDocumentItem> item);

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    EmsResultEntity insertVerifyInvInventoryDocument(InvInventoryDocument invInventoryDocument);

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑 ( 多作业类型出库 )
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    EmsResultEntity insertVerifyInvInventoryDocumentByMovementType(InvInventoryDocument invInventoryDocument);

    /**
     * 移库方式为两步法且入库状态为未入库的移库进行入库
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    void yikuInStock(InvInventoryDocument invInventoryDocument);

    /**
     * 仓库物料信息表设置使用频率
     *
     * @param invStorehouseMaterialList 仓库物料信息表
     * @return 结果
     */
    int storehouseMaterialSetUsage(List<InvStorehouseMaterial> invStorehouseMaterialList, String usageFrequencyFlag);

    /**
     * 明细页签导入明细列表
     * @param file 文件
     * @return 列表结果
     */
    EmsResultEntity importItemList(MultipartFile file, String documentCategory);

    /**
     * 查询商品领用物料统计报表
     *
     * @param request
     * @return
     */
    List<InvInventoryProductUserMaterial> productUserMaterialStatistics(InvInventoryProductUserMaterial request);
}
