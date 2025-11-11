package com.platform.ems.enums;

/**
 * 处理状态
 *
 * @Author linhongwei
 * @Description //TODO
 * @Date 13:25
 * @Param
 * @return
 **/
public enum HandleStatus {
    SAVE("1", "保存"),
    CHANGEAPPROVAL("2", "变更审批中"),
    SUBMIT("3", "已提交/审批中"),
    RETURNED("4", "已退回"),
    CONFIRMED("5", "已确认"),
    COMPLETED("6", "已完成"),
    CLOSED("7", "已关闭"),
    INVALID("8", "已作废"),
    CONCLUDE("9", "已结案"),
    REDDASHED("A", "已红冲"),
    POSTING("B", "已过账"),
    BG_RETURN("Z", "变更驳回"),
    CHONGX("C", "已冲销");

    private final String code;
    private final String info;

    HandleStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }


    public static boolean isTemporarySave(String handleStatus) {
        return SAVE.getCode().equals(handleStatus);
    }

    public static boolean isConfirmed(String handleStatus) {
        return CONFIRMED.getCode().equals(handleStatus);
    }
}
