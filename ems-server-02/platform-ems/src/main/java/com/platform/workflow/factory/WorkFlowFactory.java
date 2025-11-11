package com.platform.workflow.factory;

import com.platform.ems.mapper.DelDeliveryNoteMapper;
import com.platform.ems.service.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注入业务单据service,方便调用完工作流接口做业务逻辑处理
 * @author qhq
 */
@Component
@Getter
public class WorkFlowFactory {
    /**
     * 采购合同
     */
    @Autowired
    private IPurPurchaseContractService purPurchaseContractService;

    /**
     * bom
     */
    @Autowired
    private ITecBomHeadService bomService;

    /**
     * 加工采购价
     */
    @Autowired
    private IPurOutsourcePurchasePriceService purOutsourcePurchasePriceService;

    /**
     * 采购价
     */
    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;

    /**
     * 采购订单
     */
    @Autowired
    private IPurPurchaseOrderService purPurchaseOrderService;

    /**
     * 发货单
     */
    @Autowired
    private IInvGoodIssueNoteService iInvGoodIssueNoteService;

    /**
     * 发货单
     */
    @Autowired
    private IInvGoodReceiptNoteService iInvGoodReceiptNoteService;

    /**
     * 库存调整
     */
    @Autowired
    private IInvInventoryAdjustService iInvInventoryAdjustService;

    /**
     * 领退料
     */
    @Autowired
    private IInvMaterialRequisitionService iInvMaterialRequisitionService;

    /**
     * 盘点
     */
    @Autowired
    private IInvInventorySheetService iInvInventorySheetService;

    /**
     * 调拨单
     */
    @Autowired
    private IInvInventoryTransferService iInvInventoryTransferService;

    /**
     * 销售价信息
     */
    @Autowired
    private ISalSalePriceService salSalePriceService;

    /**
     * 销售订单
     */
    @Autowired
    private ISalSalesOrderService salSalesOrderService;

    /**
     * 采购议价
     */
    @Autowired
    private IPurQuoteBargainService purQuoteBargainService;

    /**
     * 加工议价
     */
    @Autowired
    private IPurOutsourceQuoteBargainService purOutsourceQuoteBargainService;

    /**
     * 申购单
     */
    @Autowired
    private IReqPurchaseRequireService reqPurchaseRequireService;

    /**
     * 生产月计划
     */
    @Autowired
    private IManMonthManufacturePlanService manMonthManufacturePlanService;

    /**
     * 生产周计划
     */
    @Autowired
    private IManWeekManufacturePlanService manWeekManufacturePlanService;

    /**
     * 生产进度日报
     */
    @Autowired
    private IManDayManufactureProgressService manDayManufactureProgressService;

    /**
     * 生产完工确认单
     */
    @Autowired
    private IManManufactureCompleteNoteService manManufactureCompleteNoteService;

    /**
     * 外发加工费结算单
     */
    @Autowired
    private IManManufactureOutsourceSettleService manManufactureOutsourceSettleService;

    /**
     * 生产订单
     */
    @Autowired
    private IManManufactureOrderService manManufactureOrderService;

    /**
     * 外发加工发料单
     */
    @Autowired
    private IDelOutsourceMaterialIssueNoteService delOutsourceMaterialIssueNoteService;

    /**
     * 外发加工收料单
     */
    @Autowired
    private IDelOutsourceDeliveryNoteService delOutsourceDeliveryNoteService;

    /**
     * 成本核算
     */
    @Autowired
    private ICosProductCostService cosProductCostService;

    /**
     * 甲供料结算单
     */
    @Autowired
    private IInvOwnerMaterialSettleService invOwnerMaterialSettleService;

    /**
     * 销售合同
     */
    @Autowired
    private ISalSaleContractService salSaleContractService;

    /**
     * 资产管理
     */
    @Autowired
    private IAssAssetRecordService assAssetRecordService;

    /**
     * 资金流水
     */
    @Autowired
    private IFunFundAccountService funFundAccountService;

    /**
     * 资金账号
     */
    @Autowired
    private IFunFundRecordService funFundRecordService;

    /**
     * 供应商注册
     */
    @Autowired
    private IBasVendorRegisterService basVendorRegisterService;

    /**
     * 封样记录(标准封样、产前封样)
     */
    @Autowired
    private ITecRecordFengyangService tecRecordFengyangService;

    /**
     * 技术转移
     */
    @Autowired
    private ITecRecordTechtransferService tecRecordTechtransferService;

    @Autowired
    private ITecMaterialSizeService tecMaterialSizeService;

    /**
     * 商品道序
     */
    @Autowired
    private IPayProductProcessStepService payProductProcessStepService;

    /**
     * 完工量申报
     */
    @Autowired
    private IPayProcessStepCompleteService payProcessStepCompleteService;

    /**
     * 考勤信息
     */
    @Autowired
    private IPayWorkattendRecordService payWorkattendRecordService;

    /**
     * 工资单
     */
    @Autowired
    private IPaySalaryBillService paySalaryBillService;
    /**
     * 交货单
     */
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;

    /**
     * 图稿批复
     */
    @Autowired
    private IDevDesignDrawFormService devDesignDrawFormService;

    /**
     * 外采样报销单
     */
    @Autowired
    private ISamOsbSampleReimburseService samOsbSampleReimburseService;

    /**
     * 打样准许
     */
    @Autowired
    private IDevMakeSampleFormService devMakeSampleFormService;

    /**
     * 样品评审
     */
    @Autowired
    private IDevSampleReviewFormService devSampleReviewFormService;
    @Autowired
    private IDelDeliveryNoteService delDeliveryNoteService;

    @Autowired
    private IBasMaterialService materialService;
    /**
     * 样品借还
     */
    @Autowired
    private ISamSampleLendreturnService samSampleLendreturnService;


    /**
     * 图稿绘制单
     */
    @Autowired
    private IFrmDraftDesignService frmDraftDesignService;

    /**
     * 文案视觉单
     */
    @Autowired
    private IFrmDocumentVisionService frmDocumentVisionService;

    /**
     * 拍照样获取单
     */
    @Autowired
    private IFrmPhotoSampleGainService frmPhotoSampleGainService;

    /**
     * 到货通知单
     */
    @Autowired
    private IFrmArrivalNoticeService frmArrivalNoticeService;

    /**
     * 样品评审单 初审/终审
     */
    @Autowired
    private IFrmSampleReviewService frmSampleReviewService;

    /**
     * 新品试销计划单
     */
    @Autowired
    private IFrmNewproductTrialsalePlanService frmNewproductTrialsalePlanService;

    /**
     * 试销结果单
     */
    @Autowired
    private IFrmTrialsaleResultService frmTrialsaleResultService;

}
