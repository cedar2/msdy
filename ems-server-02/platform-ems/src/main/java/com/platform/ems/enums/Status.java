package com.platform.ems.enums;

 /**
  * 启用/停用状态
  * @Author linhongwei
  * @Description //TODO
  * @Date  13:25
  * @Param
  * @return
  **/
public enum Status {
    ENABLE("1", "启用"),
    DISABLE("2", "停用");

    private final String code;
    private final String info;

    Status(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
