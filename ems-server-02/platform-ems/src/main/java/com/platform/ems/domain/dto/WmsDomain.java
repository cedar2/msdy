package com.platform.ems.domain.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "response")
public class WmsDomain {

    @JacksonXmlProperty
    public String flag;

    @JacksonXmlProperty
    public String code;

    @JacksonXmlProperty
    public String message;

    public WmsDomain() {}

    public WmsDomain(String flag, String code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public WmsDomain error(String message) {
        return new WmsDomain("failure", "500", message);
    }

    public WmsDomain success(String message) {
        return new WmsDomain("success", "200", message);
    }

    public WmsDomain defaultResponse() {
        return new WmsDomain("success", "200", "操作成功");
    }
}
