package com.platform.common.core.domain;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.entity.*;

/**
 * Treeselect树结构实体类
 *
 * @author platform
 */
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {

    }

    public TreeSelect(SysDept dept) {
        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TreeSelect> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSelect> children) {
        this.children = children;
    }

    /**
     * 拉链标识
     */
    private String zipperFlag;

    private String nodeIdField;

    private String nodeNameField;

    /**
     * 组织架构树形构造函数
     *
     * @param organizationInfor
     */
    public TreeSelect(SysOrg organizationInfor) {
        this.id = organizationInfor.getNodeSid();
        this.label = organizationInfor.getNodeName();
        this.children = organizationInfor.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 物料分类树形构造函数
     *
     * @param conMaterialClass
     */
    public TreeSelect(ConMaterialClass conMaterialClass) {
        this.id = conMaterialClass.getMaterialClassSid();
        this.label = conMaterialClass.getNodeName();
        this.zipperFlag = conMaterialClass.getZipperFlag();
        this.children = conMaterialClass.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }


    /**
     * 国家区域树形构造函数
     *
     * @param conCountryRegion
     */
    public TreeSelect(ConCountryRegion conCountryRegion) {
        this.id = conCountryRegion.getCountryRegionSid();
        this.label = conCountryRegion.getNodeName();
        this.children = conCountryRegion.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }



    public String getZipperFlag() {
        return zipperFlag;
    }

    public void setZipperFlag(String zipperFlag) {
        this.zipperFlag = zipperFlag;
    }


}
