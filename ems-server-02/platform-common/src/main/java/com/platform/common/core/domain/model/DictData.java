package com.platform.common.core.domain.model;

import com.platform.common.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * 字典数据表 sys_dict_data
 *
 * @author platform
 */
public class DictData implements Serializable {


    private List<DictData> dataList;

    private String dictType;
    /**
     * 字典标签
     */
    @Excel(name = "字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @Excel(name = "字典键值")
    private String dictValue;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "处理状态")
    private String handleStatus;

    @Override
    public boolean equals(Object obj){
        return obj instanceof DictData&& this.dictType != null && this.dictType.equals(((DictData)obj).getDictType())
                &&this.dictValue != null && this.dictValue.equals(((DictData)obj).getDictValue());
    }


    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public List<DictData> getDataList() {
        return dataList;
    }

    public void setDataList(List<DictData> dataList) {
        this.dataList = dataList;
    }

    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }
}

