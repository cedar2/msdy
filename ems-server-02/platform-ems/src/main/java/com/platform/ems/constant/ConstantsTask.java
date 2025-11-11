package com.platform.ems.constant;

import com.platform.common.log.enums.BusinessType;
import com.platform.ems.enums.HandleStatus;

/**
 * 工作流任务节点名称
 *
 * @author chenkw
 */
public class ConstantsTask {

    /**
     * 节点 核价录入
     */
    public static final String TASK_NAME_SUBMIT = "驳回到提交人";

    /**
     * 节点 核价录入
     */
    public static final String TASK_NAME_HJLR = "核价录入";

    /**
     * 节点 议价录入
     */
    public static final String TASK_NAME_YJLR = "议价录入";

    /**
     * 节点 议价录入
     */
    public static final String TASK_NAME_YJSP1 = "议价审批1";

    /**
     * 根据操作类型 返回 对应的 处理状态
     */
    public static String backHandleByBusiness(String business){
        if (BusinessType.SUBMIT.getValue().equals(business)
                || BusinessType.APPROVED.getValue().equals(business)) {
            return HandleStatus.SUBMIT.getCode();
        } else if (BusinessType.DISAPPROVED.getValue().equals(business)) {
            return HandleStatus.RETURNED.getCode();
        }
        return null;
    }
}
