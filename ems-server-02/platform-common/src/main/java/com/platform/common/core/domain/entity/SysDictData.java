package com.platform.common.core.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.platform.common.annotation.Excel;
import com.platform.common.annotation.Excel.ColumnType;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 字典数据表 sys_dict_data
 *
 * @author platform
 */
public class SysDictData extends BaseEntity
{

    public SysDictData(){

    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof SysDictData && this.dictType != null && this.dictType.equals(((SysDictData)obj).getDictType())
                &&this.dictValue != null && this.dictValue.equals(((SysDictData)obj).getDictValue());
    }

    public SysDictData(String dictType,String clientId){
        this.dictType=dictType;
        this.clientId=clientId;
    }

    /**
     * 字典编码
     */
    @Excel(name = "字典编码", cellType = ColumnType.NUMERIC)
    private Long dictCode;

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

    /**
     * 字典排序
     */
    @Excel(name = "字典排序", cellType = ColumnType.NUMERIC)
    private Long dictSort;

    /**
     * 外围系统数据标签
     */
    @Excel(name = "外围系统数据标签")
    private String otherSystemLabel;

    /**
     * 外围系统数据键值
     */
    @Excel(name = "外围系统数据键值")
    private String otherSystemValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格字典样式
     */
    private String listClass;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String handleStatus;

    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date createTime;

    @Excel(name = "租户ID")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String clientId;

    @TableField(exist = false)
    private String[] clientIdList;

    public String[] getClientIdList() {
        return clientIdList;
    }

    public void setClientIdList(String[] clientIdList) {
        this.clientIdList = clientIdList;
    }

    @TableField(exist = false)
    private String dictLevel;

    public String getDictLevel() {
        return dictLevel;
    }

    public void setDictLevel(String dictLevel) {
        this.dictLevel = dictLevel;
    }

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getDictCode() {
        return dictCode;
    }

    public void setDictCode(Long dictCode) {
        this.dictCode = dictCode;
    }

    public Long getDictSort() {
        return dictSort;
    }

    public void setDictSort(Long dictSort) {
        this.dictSort = dictSort;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    @NotBlank(message = "字典标签不能为空")
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符")
    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    @NotBlank(message = "字典键值不能为空")
    @Size(min = 0, max = 100, message = "字典键值长度不能超过100个字符")
    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型长度不能超过100个字符")
    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    @Size(min = 0, max = 100, message = "样式属性长度不能超过100个字符")
    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getOtherSystemLabel() {
        return otherSystemLabel;
    }

    public void setOtherSystemLabel(String otherSystemLabel) {
        this.otherSystemLabel = otherSystemLabel;
    }

    public String getOtherSystemValue() {
        return otherSystemValue;
    }

    public void setOtherSystemValue(String otherSystemValue) {
        this.otherSystemValue = otherSystemValue;
    }

    public String getListClass() {
        return listClass;
    }

    public void setListClass(String listClass) {
        this.listClass = listClass;
    }

    public boolean getDefault() {
        return UserConstants.YES.equals(this.isDefault) ? true : false;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("dictCode", getDictCode())
                .append("dictSort", getDictSort()).append("dictLabel", getDictLabel())
                .append("dictValue", getDictValue()).append("dictType", getDictType()).append("cssClass", getCssClass())
                .append("listClass", getListClass()).append("isDefault", getIsDefault()).append("status", getStatus())
                .append("createBy", getCreateBy()).append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy()).append("updateTime", getUpdateTime()).append("remark", getRemark())
                .toString();
    }
}
