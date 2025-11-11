package com.platform.ems.util;

import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.enums.FormType;

/**
 * @description:
 * @author: Hu JJ
 * @date: 2021-12-14
 */
public class WorkbenchUtil {

    public static Long setMenuId(String formType, String formId) {
        if (FormType.PurchaseContract.getCode().equals(formType)) {
            return ConstantsWorkbench.purchase_contract;
        } else if (FormType.SaleContract.getCode().equals(formType)) {
            return ConstantsWorkbench.sale_contract;
        } else if (FormType.PurchasePrice.getCode().equals(formType)) {
            return ConstantsWorkbench.purchase_price;
        }
        return null;
    }

    /**
     * 根据单据类型返回单据详情页面在menu表中的菜单名字
     * @param formType
     * @return
     */
    public static String backMenuByFormType(String formType){
        if (formType.equals(FormType.DraftDesign.getCode())) {
            return ConstantsWorkbench.TODO_FRM_DRAFT_DESIGN_MENU_NAME;
        } else if (formType.equals(FormType.DocumentVision.getCode())) {
            return ConstantsWorkbench.TODO_FRM_DOC_VIS_MENU_NAME;
        } else if (formType.equals(FormType.PhotoSampleGain.getCode())) {
            return ConstantsWorkbench.TODO_FRM_PHO_SAM_GAIN_MENU_NAME;
        } else if (formType.equals(FormType.ArrivalNotice.getCode())) {
            return ConstantsWorkbench.TODO_FRM_ARR_NOTICE_MENU_NAME;
        } else if (formType.equals(FormType.SampleReviewCs.getCode())) {
            return ConstantsWorkbench.TODO_FRM_SAM_REV_CS_MENU_NAME;
        } else if (formType.equals(FormType.SampleReviewZs.getCode())) {
            return ConstantsWorkbench.TODO_FRM_SAM_REV_ZS_MENU_NAME;
        } else if (formType.equals(FormType.NewproductTrialsalePlan.getCode())) {
            return ConstantsWorkbench.TODO_FRM_NEW_TRI_PLAN_MENU_NAME;
        } else if (formType.equals(FormType.TrialsaleResult.getCode())) {
            return ConstantsWorkbench.TODO_FRM_TRIAL_RESULT_MENU_NAME;
        } else if (formType.equals(FormType.PayBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_PAY_BILL;
        }  else if (formType.equals(FormType.ReceivableBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_RECEIVALE_BILL;
        }  else if (formType.equals(FormType.VendorDeductionBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_VEN_DEDUC_INFO;
        }  else if (formType.equals(FormType.CustomerDeductionBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_CUS_DEDUC_INFO;
        }  else if (formType.equals(FormType.VendorCashPledgeBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_VEN_CASH_INFO;
        }  else if (formType.equals(FormType.CustomerCashPledgeBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_CUS_CASH_INFO;
        }  else if (formType.equals(FormType.VendorFundsFreezeBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_VEN_FREEZE_INFO;
        }  else if (formType.equals(FormType.CustomerFundsFreezeBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_CUS_FREEZE_INFO;
        }  else if (formType.equals(FormType.VendorAccountAdjustBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_VEN_ADJ_INFO;
        }  else if (formType.equals(FormType.CustomerAccountAdjustBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_CUS_ADJ_INFO;
        }  else if (formType.equals(FormType.PaymentEstimationAdjustBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_VEN_ADJ_INFO;
        }  else if (formType.equals(FormType.ReceiptEstimationAdjustBill.getCode())) {
            return ConstantsWorkbench.TODO_FIN_CUS_ADJ_INFO;
        }  else if (formType.equals(FormType.ProductProcessStep.getCode())) {
            return ConstantsWorkbench.TODO_PRO_STEP_INFO_MENU_NAME;
        } else if (formType.equals(FormType.ProductProcessStepUpdate.getCode())) {
            return ConstantsWorkbench.TODO_UP_PRO_STEP_INFO_MENU_NAME;
        } else if (formType.equals(FormType.OutsourceBill.getCode())) {
            return ConstantsWorkbench.TODO_MAN_OUT_SET_MENU_NAME;
        } else if (formType.equals(FormType.PurchaseRequire.getCode())) {
            return ConstantsWorkbench.TODO_PUR_REQUIRE_INFO;
        }
        return null;
    }
}
