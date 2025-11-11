package com.platform.ems.enums;

public enum FormType {

	CGDD_BG("CGDD_BG","采购订单_变更","0"),
	CGHT_BG("CGHT_BG","采购合同_变更","0"),
	CGJ_BG("CGJ_BG","采购价_变更","0"),
	JGCGJ_BG("JGCGJ_BG","加工采购价_变更","0"),
	XSDD_BG("XSDD_BG","销售订单_变更","0"),
	XSHT_BG("XSHT_BG","销售合同_变更","0"),
	XSJ_BG("XSJ_BG","销售价_变更","0"),
	CQFY_BG("CQFY_BG","产前封样_变更","0"),
	TGPF_BG("TGPF_BG","图稿批复_变更","0"),
	DYZX_BG("DYZX_BG","打样准许_变更","0"),
	YPPS_BG("YPPS_BG","样品评审_变更","0"),
	JSZY_BG("JSZY_BG","技术转移_变更","0"),
	AssetRecord_BG("AssetRecord_BG","资产流水_变更","0"),
	FundAccount_BG("FundAccount_BG","资金账号_变更","0"),
	FundRecord_BG("FundRecord_BG","资金流水_变更","0"),
	PurchaseContract("PurchaseContract","采购合同","0"),
	Bom("Bom","Bom","0"),
	Model("Model","版型档案","0"),
	Customer("Customer","客户档案","0"),
	Vendor("Vendor","供应商档案","0"),
	Material("Material","&商品&服务档案","0"),
	MaterialPackage("MaterialPackage","包","0"),
	Company("Company","公司档案","0"),
	CompanyDepart("CompanyDepart","部门","0"),
	CompanyPosition("CompanyPosition","岗位","0"),
	CompanyStaff("CompanyStaff","员工档案","0"),
	MaterialSize("MaterialSize","商品尺寸","0"),
	ProductCost("ProductCost","成本核算","0"),
	SaleContract("SaleContract","销售合同","0"),
	SalePrice("SalePrice","销售价","0"),
	LaborSalePrice("LaborSalePrice","加工销售价","0"),
	SalesOrder("SalesOrder","销售订单","0"),
	PurchaseRequire("PurchaseRequire","采购申请单","0"),
	PurchaseSource("PurchaseSource","采购货源清单","0"),
	Inquiry("Inquiry","询价单","0"),
	OutsourceInquiry("OutsourceInquiry","加工询价单","0"),
	QuoteBargain("QuoteBargain","采购报核议价单(报价/核价/议价)","0"),
	OutsourceQuoteBargain("OutsourceQuoteBargain","加工报核议价单(报价/核价/议价)","0"),
	PurchasePrice("PurchasePrice","采购价","0"),
	OutsourcePurchasePrice("OutsourcePurchasePrice","加工采购价","0"),
	PurchaseOrder("PurchaseOrder","采购订单","0"),
	PurchaseOrderVendor("PurchaseOrderVendor","供应商寄售结算单","0"),
	DeliveryNote("DeliveryNote","采购交货单/销售发货单","0"),
	OutsourceGrantBill("OutsourceGrantBill","外发加工发料单","0"),
	OutsourceDeliveryNote("OutsourceDeliveryNote","外发加工收料单","0"),
	ManufactureOrder("ManufactureOrder","生产订单","0"),
	MonthManufacturePlan("MonthManufacturePlan","生产月计划","0"),
	WeekManufacturePlan("WeekManufacturePlan","生产周计划","0"),
	ManDayManufactureProgress("ManDayManufactureProgress","生产进度日报","0"),
	ManufactureCompleteNote("ManufactureCompleteNote","生产完工确认单","0"),
	OutsourceBill("OutsourceBill","外发加工费结算单","0"),
	GoodReceiptNote("GoodReceiptNote","收货单","0"),
	GoodIssueNote("GoodIssueNote","发货单","0"),
	MaterialRequisition("MaterialRequisition","领退料单","0"),
	InventoryTransfer("InventoryTransfer","调拨单","0"),
	InventoryAdjust("InventoryAdjust","库存调整单","0"),
	CorssColor("CorssColor","串色串码","0"),
	InventorySheet("InventorySheet","盘点单","0"),
	PurchaseInvoice("PurchaseInvoice","采购发票","0"),
	SaleInvoice("SaleInvoice","销售发票","0"),
	PayBill("PayBill","付款单","0"),
	ReceivableBill("ReceivableBill","收款单","0"),
	VendorDeductionBill("VendorDeductionBill","供应商扣款单","0"),
	CustomerDeductionBill("CustomerDeductionBill","客户扣款单","0"),
	VendorAccountAdjustBill("VendorAccountAdjustBill","供应商调账单","0"),
	CustomerAccountAdjustBill("CustomerAccountAdjustBill","客户调账单","0"),
	VendorAccountBalanceBill("VendorAccountBalanceBill","供应商账互抵单","0"),
	CustomerAccountOffsetBill("CustomerAccountOffsetBill","客户账互抵单","0"),
	VendorCashPledgeBill("VendorCashPledgeBill","供应商押金","0"),
	CustomerCashPledgeBill("CustomerCashPledgeBill","客户押金","0"),
	VendorFundsFreezeBill("VendorFundsFreezeBill","供应商暂押款","0"),
	CustomerFundsFreezeBill("CustomerFundsFreezeBill","客户暂押款","0"),
	VendorMonthAccountBill("VendorMonthAccountBill","供应商月度账单","0"),
	VendorFinanceList("VendorFinanceList","供应商财务台帐","0"),
	CustomerMonthAccountBill("CustomerMonthAccountBill","客户月度账单","0"),
	CustomerFinanceList("CustomerFinanceList","客户财务台帐","0"),
	ProductCostSale("ProductCostSale","销售产前成本核算","0"),
	ProductCostPurchase("ProductCostPurchase","采购产前成本核算","0"),
	InvOwnerMaterialSettle("InvOwnerMaterialSettle","甲供料结算单","0"),
	PaymentEstimationAdjustBill("YFZGTJL","应付暂估调价量","0"),
	ReceiptEstimationAdjustBill("YSZGTJL","应收暂估调价量","0"),
	AssetRecord("AssetRecord","资产流水","0"),
	FundAccount("FundAccount","资金账号","0"),
	FundRecord("FundRecord","资金流水","0"),
	ProductProcessStep("ProductProcessStep","商品道序","0"),
	ProductProcessStepUpdate("ProductProcessStepUpdate","商品道序变更","0"),
	TGPF("TGPF","图稿批复","0"),
	DYZX("DYZX","打样准许","0"),
	YPPS("YPPS","样品评审","0"),
	YPPD("YPPD","样品盘点","0"),
	YPJH("YPJH","样品借还","0"),
	BZFY("BZFY","标准封样","0"),
	CQFY("CQFY","产前封样","0"),
	JSZY("JSZY","技术转移","0"),
	GZD("GZD","工资单","0"),
	KQXX("KQXX","考勤","0"),
	WGLSB("WGLSB","完工量申报","0"),
	GYSZC("GYSZC","供应商注册","1"),
	WCYBXD("WCYBXD","外采样报销单","0"),
	DraftDesign("DraftDesign","图稿绘制单","0"),
	PhotoSampleGain("PhotoSampleGain","视觉设计单","0"),
	DocumentVision("DocumentVision","文案脚本单","0"),
	ArrivalNotice("ArrivalNotice","到货通知单","0"),
	SampleReviewCs("SampleReviewCs","样品初审单","0"),
	SampleReviewZs("SampleReviewZs","样品终审单","0"),
	NewproductTrialsalePlan("NewproductTrialsalePlan","新品试销计划单","0"),
	TrialsaleResult("TrialsaleResult","试销结果单","0"),
	;

	private final String code;
    private final String info;
	/** 1：开放，不需要token令牌； */
	private final String status;

    FormType(String code, String info, String status) {
        this.code = code;
        this.info = info;
		this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

	public String getStatus() {return status;}

}
