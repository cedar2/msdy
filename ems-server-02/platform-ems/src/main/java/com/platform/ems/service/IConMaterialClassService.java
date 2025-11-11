package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.ConMaterialClass;

import java.util.List;

/**
 * 物料分类Service接口
 *
 * @author linhongwei
 * @date 2021-09-29
 */
public interface IConMaterialClassService extends IService<ConMaterialClass> {
    /**
     * 查询物料分类
     *
     * @param materialClassSid 物料分类ID
     * @return 物料分类
     */
    public ConMaterialClass selectConMaterialClassById(Long materialClassSid);

    /**
     * 查询物料分类列表
     *
     * @param conMaterialClass 物料分类
     * @return 物料分类集合
     */
    public List<ConMaterialClass> selectConMaterialClassList(ConMaterialClass conMaterialClass);

    /**
     * 查询物料分类列表
     *
     * @param conMaterialClass 物料分类
     * @return 物料分类集合
     */
    public List<ConMaterialClass> selectConMaterialClassListByMaterialType(ConMaterialClass conMaterialClass);

    /**
     * 新增物料分类
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    public int insertConMaterialClass(ConMaterialClass conMaterialClass);

    /**
     * 修改物料分类
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    public int updateConMaterialClass(ConMaterialClass conMaterialClass);

    /**
     * 变更物料分类
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    public int changeConMaterialClass(ConMaterialClass conMaterialClass);

    /**
     * 批量删除物料分类
     *
     * @param materialClassSids 需要删除的物料分类ID
     * @return 结果
     */
    public int deleteConMaterialClassByIds(List<Long> materialClassSids);

    /**
     * 启用/停用
     *
     * @param conMaterialClass
     * @return
     */
    int changeStatus(ConMaterialClass conMaterialClass);

    /**
     * 更改确认状态
     *
     * @param conMaterialClass
     * @return
     */
    int check(ConMaterialClass conMaterialClass);

    /**
     * 校验非同级是否存在同名
     */
    ConMaterialClass selectConMaterialClassByName(ConMaterialClass conMaterialClass);

    /**
     * 获取物料分类下拉列表
     */
    List<ConMaterialClass> getConMaterialClassList(ConMaterialClass conMaterialClass);
}
