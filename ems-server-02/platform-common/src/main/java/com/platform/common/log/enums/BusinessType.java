package com.platform.common.log.enums;

/**
 * 业务操作类型
 *
 * @author platform
 */
public enum BusinessType {

    /**
     * 新建
     */
    INSERT("XJ", "新建"),

    /**
     * 编辑
     */
    UPDATE("BJ", "编辑"),

    /**
     * 删除
     */
    DELETE("SC", "删除"),

    /**
     * 查看
     */
    QUERY("CK", "查看"),

    /**
     * 暂存
     */
    SAVE("ZC", "暂存"),

    /**
     * 变更
     */
    CHANGE("BG", "变更"),

    /**
     * 取消变更
     */
    CANCEL_CHANGE("QXBG", "取消变更"),

    /**
     * 确认
     */
    CONFIRM("QR", "确认"),

    /**
     * 确认
     */
    CHECK("QR","确认"),

    /**
     * 退回
     */
    RETURN("TH","退回"),

    /**
     * 启用
     */
    ENABLE("QY", "启用"),

    /**
     * 停用
     */
    DISENABLE("TY", "停用"),

    /**
     * 提交
     */
    SUBMIT("TJ", "提交"),

    /**
     * 流转
     */
    NEXT("LZ", "流转"),

    /**
     * 更新价格
     */
    PRICE("GXJG", "更新价格"),

    /**
     * 更新金额
     */
    AMOUNT("GXJE", "更新金额"),

    /**
     * 完成
     */
    COMPLETED ("WC", "完成"),

    /**
     * 审批通过
     */
    APPROVED("SPTG", "审批通过"),

    /**
     * 审批驳回
     */
    DISAPPROVED("SPBH", "审批驳回"),

    /**
     * 结案
     */
    CONCLUDE("JA", "结案"),


    /**
     * 导入
     */
    IMPORT("DR", "导入"),

    /**
     * 导出
     */
    EXPORT("DC", "导出"),

    /**
     * 授权
     */
    GRANT("SQ", "授权"),

    /**
     * 强退用户
     */
    FORCE("QT", "强制退出"),

    /**
     * 关闭
     */
    CLOSE("GB", "关闭"),

    /**
     * 作废
     */
    CANCEL("ZF", "作废"),

    /**
     * 红冲
     */
    REDDASHED("HC", "红冲"),

    /**
     * 冲销
     */
    CHONGXIAO("CX", "冲销"),

    /**
     * 支付
     */
    PAY("ZHIF", "支付"),

    /**
     * 到账
     */
    RECEIPT("DAOZ", "到账"),

    /**
     * 过账
     */
    POSTING("GZ", "过账"),

    /**
     * 清空缓存
     */
    CLEAN("CLEAN", "清空缓存"),

    /**
     * 其他
     */
    OTHER("OTHER", "其他"),

    /**
     * 其他
     */
    QITA("QITA", "其他"),

    /**
     * 启用停用
     */
    ENBLEORDISABLE("ENBLEORDISABLE", "启用/停用"),

    /**
     * 改变处理状态
     */
    HANDLE("HANDLE", "处理状态"),

    /**
     * 生成代码
     */
    GENCODE("GENCODE", "生成代码"),
    WLTH("WLTH", "物料替换"),

    /**
     * 审批
     */
    APPROVAL("SP","审批"),
    /**
     * 发送邮件
     */
    MAILSENT("MAILSENT", "发送邮件"),
    /**
     * 流转
     */
    LZ("LZ","流转");

    private final String value;

    private final String name;

    BusinessType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
