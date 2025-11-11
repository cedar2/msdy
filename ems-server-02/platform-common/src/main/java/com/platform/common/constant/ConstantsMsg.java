package com.platform.common.constant;

/**
 * 提示信息
 */
public class ConstantsMsg {

    /**
     * 当前是保存状态，请及时处理！
     */
    public static final String TODO_SAVE_SUFFIX = "当前是保存状态，请及时处理！";
    /**
     * 被退回！
     */
    public static final String TODO_HAS_BACK_SUFFIX = "被退回！";
    /**
     * 被退回，请及时跟进！
     */
    public static final String TODO_BACK_SUFFIX = "被退回，请及时跟进！";
    /**
     * 待处理，请及时跟进！
     */
    public static final String TODO_DEAL_SUFFIX = "待处理，请及时跟进！";
    /**
     * 待分配处理人！
     */
    public static final String TODO_PROJECT_TASK_HANDLER_SUFFIX = "待分配处理人！";
    /**
     * 待意向金登记！
     */
    public static final String TODO_ADD_MONEY_RECORD_SUFFIX = "待意向金登记！";
    /**
     * 待财务确认！
     */
    public static final String TODO_CONFIRM_MONEY_RECORD_SUFFIX = "待财务确认！";
    /**
     * 待签约！
     */
    public static final String TODO_DAI_SIGN_SUFFIX = "待签约！";
    /**
     * 待上传附件！
     */
    public static final String TODO_ATTACH_UPLOAD_SUFFIX = "待上传附件！";
    /**
     * 待创建项目！
     */
    public static final String TODO_DAI_CREATE_PROJECT_SUFFIX = "待创建项目！";

    /**
     * 找不到该
     */
    public static final String NOT_FOUND_INFO_DATA = "找不到该";
    /**
     * 请刷新页面
     */
    public static final String REFRESH_PAGE = "请刷新页面";
    /**
     * 请选择行
     */
    public static final String PLEASE_SELECT_ROW = "请选择行";
    /**
     * 操作失败
     */
    public static final String OPERATOR_ERR = "操作失败";
    /**
     * 参数缺失
     */
    public static final String PARAMS_LOST = "参数缺失";
    /**
     * 报铺申请重置匹配结果
     */
    public static final String APPLY_MATCH_RESET_RESULT = "报铺申请重置匹配结果";
    /**
     * 报铺申请重新匹配
     */
    public static final String APPLY_MATCH_RESET_REMARK = "报铺申请重新匹配";

    /**
     * 待分配回访人员
     */
    public static final String JOIN_APPLY_RETURN_VISITY = "待分配回访人员";

    public static final String FOLLOWUP_VISITS = "待回访跟进";
    public static final String CREATE_AGREEMENT = "待创建意向协议";
    public static final String FOLLOWUP_UPDATE = "回访人员更新";
    /**
     * 门店位置一级类型
     */
    public static final String POSITION_FIRST_CODE_IS_EMPTY = "门店位置一级类型编码为空";
    public static final String POSITION_FIRST_NAME_IS_EMPTY = "门店位置一级类型名称为空";
    public static final String POSITION_FIRST_CODE_REPEAT = "门店位置一级类型编码已存在";
    public static final String POSITION_FIRST_CODE_NOT_EXIST = "门店位置一级类型编码不存在";
    public static final String POSITION_FIRST_CODE_NOT_NL = "门店位置一级类型编码只能输入数字和字母";
    public static final String POSITION_FIRST_NAME_REPEAT = "门店位置一级类型名称已存在";
    public static final String POSITION_FIRST_TYPE_STATUS_DEACTIVATE = "门店位置一级类型的状态停用";
    /**
     * 门店位置二级类型
     */
    public static final String POSITION_SECOND_CODE_REPEAT = "门店位置二级类型编码已存在";
    public static final String POSITION_SECOND_NAME_REPEAT = "门店位置二级类型名称已存在";
    public static final String POSITION_SECOND_CODE_NOT_EXIST = "门店位置二级类型编码不存在";

    /**
     * 选址评估
     */
    public static final String ADDR_EVALUATE_NUM_NOT_NL = "选址评估项目编号只能输入数字和字母";
    public static final String ADDR_EVALUATE_SCORE_NOT_LESS_ZERO = "选址评估最高分数不能为负数";
    public static final String ADDR_EVALUATE_NUM_IS_EXIST = "选址评估项目编号已存在";
    public static final String ADDR_EVALUATE_ITEM_NAME_IS_EXIST = "选址评估调查项目名已存在";
    public static final String ADDR_EVALUATE_CODE_NOT_EXIST = "选址评估项目不存在";

    /**
     * 选址评估模板
     */
    public static final String ADDR_EVALUATE_TEMPLATE_EXIST_NOT_SAVE = "存在未保存的选址评估模板";
    public static final String ADDR_EVALUATE_TEMPLATE_CODE_NOT_NL = "选址评估模板编号只能输入数字和字母";
    public static final String ADDR_EVALUATE_TEMPLATE_CODE_IS_EXIST = "选址评估模板编号已存在";
    public static final String ADDR_EVALUATE_TEMPLATE_NAME_IS_EXIST = "选址评估模板名称已存在";
    public static final String ADDR_EVALUATE_TEMPLATE_IS_NOT_EXIST = "选址评估模板不存在";
    public static final String ADDR_EVALUATE_TEMPLATE_CODE_NOT_UPDATE = "选址评估模板编号不能修改";
    public static final String ADDR_EVALUATE_TEMPLATE_SAVA_STATUS_ONLY_EDIT = "只有状态为保存的选址评估模板编号才能编辑";
    public static final String ADDR_EVALUATE_TEMPLATE_CHECK_STATUS_ONLY_CHANGE = "只有状态为已确认的选址评估模板编号才能变更";
    public static final String ADDR_EVALUATE_TEMPLATE_CHANGE_ONLY_CHECK_STATUS = "选址评估模板变更只能进行确认操作";

    /**
     * 任务模版对照关系
     */
    public static final String CON_TASK_TEMPLATE_COMPARE_IS_EXIST = "此组合已存在对照关系";

    /**
     * 选址评估模板与区域对照关系
     */
    public static final String CON_ADDR_TEMPLATE_COMPARE_IS_EXIST = "所属区域已存在对照关系！";

    /**
     * 查看详情找不到数据
     */
    public static String INFO_NOT_FOUND_DATA(String title) {
        return NOT_FOUND_INFO_DATA + title + REFRESH_PAGE;
    }
}

