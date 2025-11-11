package com.platform.ems.workflow.util;

import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.enums.FormType;

/**
 * @author qhq
 */
public class WorkFlowUtil {

    /**
     * 根据单据类型返回单据表名
     * @param formType
     * @return
     */
    public static String backTableByFormType(String formType){
        if (formType.equals(FormType.DraftDesign.getCode())) {
            return ConstantsTable.TABLE_FRM_DRAFT_DESIGN;
        } else if (formType.equals(FormType.DocumentVision.getCode())) {
            return ConstantsTable.TABLE_FRM_DOCUMENT_VISION;
        } else if (formType.equals(FormType.PhotoSampleGain.getCode())) {
            return ConstantsTable.TABLE_FRM_PHOTO_SAMPLE_GAIN;
        } else if (formType.equals(FormType.ArrivalNotice.getCode())) {
            return ConstantsTable.TABLE_FRM_ARRIVAL_NOTICE;
        } else if (formType.equals(FormType.SampleReviewCs.getCode())) {
            return ConstantsTable.TABLE_FRM_SAMPLE_REVIEW;
        } else if (formType.equals(FormType.SampleReviewZs.getCode())) {
            return ConstantsTable.TABLE_FRM_SAMPLE_REVIEW;
        } else if (formType.equals(FormType.NewproductTrialsalePlan.getCode())) {
            return ConstantsTable.TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN;
        } else if (formType.equals(FormType.TrialsaleResult.getCode())) {
            return ConstantsTable.TABLE_FRM_TRIALSALE_RESULT;
        } else if (formType.equals(FormType.OutsourceBill.getCode())) {
            return ConstantsTable.TABLE_MANUFACTURE_OUTSOURCE_SETTLE;
        } else if (formType.equals(FormType.PurchaseRequire.getCode())) {
            return ConstantsTable.TABLE_PURCHASE_REQUIRE;
        } else if (formType.equals(FormType.VendorDeductionBill.getCode())) {
            return ConstantsTable.TABLE_FIN_VENDOR_DEDUCTION_BILL;
        } else if (formType.equals(FormType.CustomerDeductionBill.getCode())) {
            return ConstantsTable.TABLE_FIN_CUSTOEMR_DEDUCTION_BILL;
        } else if (formType.equals(FormType.VendorCashPledgeBill.getCode())) {
            return ConstantsTable.TABLE_FIN_VENDOR_CASH_PLEDGE_BILL;
        } else if (formType.equals(FormType.CustomerCashPledgeBill.getCode())) {
            return ConstantsTable.TABLE_FIN_CUSTOEMR_CASH_PLEDGE_BILL;
        } else if (formType.equals(FormType.VendorFundsFreezeBill.getCode())) {
            return ConstantsTable.TABLE_FIN_VENDOR_FUNDS_FREEZE_BILL;
        } else if (formType.equals(FormType.CustomerFundsFreezeBill.getCode())) {
            return ConstantsTable.TABLE_FIN_CUSTOMER_FUNDS_FREEZE_BILL;
        } else if (formType.equals(FormType.PaymentEstimationAdjustBill.getCode())) {
            return ConstantsTable.TABLE_FIN_PAY_EST_ADJ_BILL;
        } else if (formType.equals(FormType.ReceiptEstimationAdjustBill.getCode())) {
            return ConstantsTable.TABLE_FIN_REC_EST_ADJ_BILL;
        } else if (formType.equals(FormType.PayBill.getCode())) {
            return ConstantsTable.TABLE_FIN_PAY_BILL;
        } else if (formType.equals(FormType.ReceivableBill.getCode())) {
            return ConstantsTable.TABLE_FIN_RECEIVABLE_BILL;
        }
        return null;
    }

    /**
     * 根据单据类型返回单据名称
     * @param formType
     * @return
     */
    public static String backNameByFormType(String formType){
        if(formType.equals(FormType.PurchaseContract.getCode())){
            return FormType.PurchaseContract.getInfo();
        }else if(formType.equals(FormType.Bom.getCode())){
            return FormType.Bom.getInfo();
        }else if(formType.equals(FormType.Model.getCode())){
            return FormType.Model.getInfo();
        }else if(formType.equals(FormType.Customer.getCode())){
            return FormType.Customer.getInfo();
        }else if(formType.equals(FormType.Vendor.getCode())){
            return FormType.Vendor.getInfo();
        }else if(formType.equals(FormType.Material.getCode())){
            return FormType.Material.getInfo();
        }else if(formType.equals(FormType.MaterialPackage.getCode())){
            return FormType.MaterialPackage.getInfo();
        }else if(formType.equals(FormType.Company.getCode())){
            return FormType.Company.getInfo();
        }else if(formType.equals(FormType.CompanyDepart.getCode())){
            return FormType.CompanyDepart.getInfo();
        }else if(formType.equals(FormType.CompanyPosition.getCode())){
            return FormType.CompanyPosition.getInfo();
        }else if(formType.equals(FormType.CompanyStaff.getCode())){
            return FormType.CompanyStaff.getInfo();
        }else if(formType.equals(FormType.MaterialSize.getCode())){
            return FormType.MaterialSize.getInfo();
        }else if(formType.equals(FormType.ProductCost.getCode())){
            return FormType.ProductCost.getInfo();
        }else if(formType.equals(FormType.SaleContract.getCode())){
            return FormType.SaleContract.getInfo();
        }else if(formType.equals(FormType.SalePrice.getCode())){
            return FormType.SalePrice.getInfo();
        }else if(formType.equals(FormType.LaborSalePrice.getCode())){
            return FormType.LaborSalePrice.getInfo();
        }else if(formType.equals(FormType.SalesOrder.getCode())){
            return FormType.SalesOrder.getInfo();
        }else if(formType.equals(FormType.PurchaseRequire.getCode())){
            return FormType.PurchaseRequire.getInfo();
        }else if(formType.equals(FormType.PurchaseSource.getCode())){
            return FormType.PurchaseSource.getInfo();
        }else if(formType.equals(FormType.ProductProcessStep.getCode())){
            return FormType.ProductProcessStep.getInfo();
        }else if(formType.equals(FormType.ProductProcessStepUpdate.getCode())){
            return FormType.ProductProcessStepUpdate.getInfo();
        }else if(formType.equals(FormType.Inquiry.getCode())){
            return FormType.Inquiry.getInfo();
        }else if(formType.equals(FormType.OutsourceInquiry.getCode())){
            return FormType.OutsourceInquiry.getInfo();
        }else if(formType.equals(FormType.QuoteBargain.getCode())){
            return FormType.QuoteBargain.getInfo();
        }else if(formType.equals(FormType.OutsourceQuoteBargain.getCode())){
            return FormType.OutsourceQuoteBargain.getInfo();
        }else if(formType.equals(FormType.PurchasePrice.getCode())){
            return FormType.PurchasePrice.getInfo();
        }else if(formType.equals(FormType.OutsourcePurchasePrice.getCode())){
            return FormType.OutsourcePurchasePrice.getInfo();
        }else if(formType.equals(FormType.PurchaseOrder.getCode())){
            return FormType.PurchaseOrder.getInfo();
        }else if(formType.equals(FormType.DeliveryNote.getCode())){
            return FormType.DeliveryNote.getInfo();
        }else if(formType.equals(FormType.OutsourceGrantBill.getCode())){
            return FormType.OutsourceGrantBill.getInfo();
        }else if(formType.equals(FormType.OutsourceDeliveryNote.getCode())){
            return FormType.OutsourceDeliveryNote.getInfo();
        }else if(formType.equals(FormType.ManufactureOrder.getCode())){
            return FormType.ManufactureOrder.getInfo();
        }else if(formType.equals(FormType.MonthManufacturePlan.getCode())){
            return FormType.MonthManufacturePlan.getInfo();
        }else if(formType.equals(FormType.WeekManufacturePlan.getCode())){
            return FormType.WeekManufacturePlan.getInfo();
        }else if(formType.equals(FormType.ManDayManufactureProgress.getCode())){
            return FormType.ManDayManufactureProgress.getInfo();
        }else if(formType.equals(FormType.ManufactureCompleteNote.getCode())){
            return FormType.ManufactureCompleteNote.getInfo();
        }else if(formType.equals(FormType.OutsourceBill.getCode())){
            return FormType.OutsourceBill.getInfo();
        }else if(formType.equals(FormType.GoodReceiptNote.getCode())){
            return FormType.GoodReceiptNote.getInfo();
        }else if(formType.equals(FormType.GoodIssueNote.getCode())){
            return FormType.GoodIssueNote.getInfo();
        }else if(formType.equals(FormType.MaterialRequisition.getCode())){
            return FormType.MaterialRequisition.getInfo();
        }else if(formType.equals(FormType.InventoryTransfer.getCode())){
            return FormType.InventoryTransfer.getInfo();
        }else if(formType.equals(FormType.InventoryAdjust.getCode())){
            return FormType.InventoryAdjust.getInfo();
        }else if(formType.equals(FormType.InventorySheet.getCode())){
            return FormType.InventorySheet.getInfo();
        }else if(formType.equals(FormType.PurchaseInvoice.getCode())){
            return FormType.PurchaseInvoice.getInfo();
        }else if(formType.equals(FormType.SaleInvoice.getCode())){
            return FormType.SaleInvoice.getInfo();
        }else if(formType.equals(FormType.PaymentEstimationAdjustBill.getCode())){
            return FormType.PaymentEstimationAdjustBill.getInfo();
        }else if(formType.equals(FormType.ReceiptEstimationAdjustBill.getCode())){
            return FormType.ReceiptEstimationAdjustBill.getInfo();
        }else if(formType.equals(FormType.PayBill.getCode())){
            return FormType.PayBill.getInfo();
        }else if(formType.equals(FormType.ReceivableBill.getCode())){
            return FormType.ReceivableBill.getInfo();
        }else if(formType.equals(FormType.VendorDeductionBill.getCode())){
            return FormType.VendorDeductionBill.getInfo();
        }else if(formType.equals(FormType.CustomerDeductionBill.getCode())){
            return FormType.CustomerDeductionBill.getInfo();
        }else if(formType.equals(FormType.VendorAccountAdjustBill.getCode())){
            return FormType.VendorAccountAdjustBill.getInfo();
        }else if(formType.equals(FormType.CustomerAccountAdjustBill.getCode())){
            return FormType.CustomerAccountAdjustBill.getInfo();
        }else if(formType.equals(FormType.VendorAccountBalanceBill.getCode())){
            return FormType.VendorAccountBalanceBill.getInfo();
        }else if(formType.equals(FormType.CustomerAccountOffsetBill.getCode())){
            return FormType.CustomerAccountOffsetBill.getInfo();
        }else if(formType.equals(FormType.VendorCashPledgeBill.getCode())){
            return FormType.VendorCashPledgeBill.getInfo();
        }else if(formType.equals(FormType.CustomerCashPledgeBill.getCode())){
            return FormType.CustomerCashPledgeBill.getInfo();
        }else if(formType.equals(FormType.VendorFundsFreezeBill.getCode())){
            return FormType.VendorFundsFreezeBill.getInfo();
        }else if(formType.equals(FormType.CustomerFundsFreezeBill.getCode())){
            return FormType.CustomerFundsFreezeBill.getInfo();
        }else if(formType.equals(FormType.VendorMonthAccountBill.getCode())){
            return FormType.VendorMonthAccountBill.getInfo();
        }else if(formType.equals(FormType.VendorFinanceList.getCode())){
            return FormType.VendorFinanceList.getInfo();
        }else if(formType.equals(FormType.CustomerMonthAccountBill.getCode())){
            return FormType.CustomerMonthAccountBill.getInfo();
        }else if(formType.equals(FormType.CustomerFinanceList.getCode())){
            return FormType.CustomerFinanceList.getInfo();
        }else if(formType.equals(FormType.ProductCostSale.getCode())){
            return FormType.ProductCostSale.getInfo();
        }else if(formType.equals(FormType.ProductCostPurchase.getCode())){
            return FormType.ProductCostPurchase.getInfo();
        }else if(formType.equals(FormType.InvOwnerMaterialSettle.getCode())){
            return FormType.InvOwnerMaterialSettle.getInfo();
        }else if(formType.equals(FormType.AssetRecord.getCode())){
            return FormType.AssetRecord.getInfo();
        }else if(formType.equals(FormType.FundAccount.getCode())){
            return FormType.FundAccount.getInfo();
        }else if(formType.equals(FormType.FundRecord.getCode())){
            return FormType.FundRecord.getInfo();
        }else if(formType.equals(FormType.TGPF.getCode())){
            return FormType.TGPF.getInfo();
        }else if(formType.equals(FormType.DYZX.getCode())){
            return FormType.DYZX.getInfo();
        }else if(formType.equals(FormType.YPPS.getCode())){
            return FormType.YPPS.getInfo();
        }else if(formType.equals(FormType.YPPD.getCode())){
            return FormType.YPPD.getInfo();
        }else if(formType.equals(FormType.YPJH.getCode())){
            return FormType.YPJH.getInfo();
        }else if(formType.equals(FormType.BZFY.getCode())){
            return FormType.BZFY.getInfo();
        }else if(formType.equals(FormType.CQFY.getCode())){
            return FormType.CQFY.getInfo();
        }else if(formType.equals(FormType.JSZY.getCode())){
            return FormType.JSZY.getInfo();
        }else if(formType.equals(FormType.GZD.getCode())){
            return FormType.GZD.getInfo();
        }else if(formType.equals(FormType.KQXX.getCode())){
            return FormType.KQXX.getInfo();
        }else if(formType.equals(FormType.WGLSB.getCode())){
            return FormType.WGLSB.getInfo();
        }else if(formType.equals(FormType.GYSZC.getCode())){
            return FormType.GYSZC.getInfo();
        }else if(formType.equals(FormType.WCYBXD.getCode())){
            return FormType.WCYBXD.getInfo();
        } else if (formType.equals(FormType.DraftDesign.getCode())) {
            return FormType.DraftDesign.getInfo();
        } else if (formType.equals(FormType.DocumentVision.getCode())) {
            return FormType.DocumentVision.getInfo();
        } else if (formType.equals(FormType.PhotoSampleGain.getCode())) {
            return FormType.PhotoSampleGain.getInfo();
        } else if (formType.equals(FormType.ArrivalNotice.getCode())) {
            return FormType.ArrivalNotice.getInfo();
        } else if (formType.equals(FormType.SampleReviewCs.getCode())) {
            return FormType.SampleReviewCs.getInfo();
        } else if (formType.equals(FormType.SampleReviewZs.getCode())) {
            return FormType.SampleReviewZs.getInfo();
        } else if (formType.equals(FormType.NewproductTrialsalePlan.getCode())) {
            return FormType.NewproductTrialsalePlan.getInfo();
        } else if (formType.equals(FormType.TrialsaleResult.getCode())) {
            return FormType.TrialsaleResult.getInfo();
        }
        return null;
    }
}
