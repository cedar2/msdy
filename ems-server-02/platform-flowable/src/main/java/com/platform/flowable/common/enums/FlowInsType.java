package com.platform.flowable.common.enums;

public enum FlowInsType {

	IN_APPROVAL("1","流程进行中"),
	END("2","流程结束");

	/**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    FlowInsType(String type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }
}
