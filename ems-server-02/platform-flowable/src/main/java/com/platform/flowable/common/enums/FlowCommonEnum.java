package com.platform.flowable.common.enums;

public enum FlowCommonEnum {

	TRUE("true","常量true"),
	FALSE("false","常量false");

	/**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    FlowCommonEnum(String type, String remark) {
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
