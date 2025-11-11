package com.platform.common.core.domain.document;

import lombok.Data;

@Data
public class OperMsg {

    public OperMsg(){

    }

    public OperMsg(String name,String alias,Object oldValue,Object newValue){
        this.alias=alias;
        this.name=name;
        this.newValue=newValue;
        this.oldValue=oldValue;
    }

    private String alias;

    private String name;

    private Object oldValue;

    private Object newValue;
}
