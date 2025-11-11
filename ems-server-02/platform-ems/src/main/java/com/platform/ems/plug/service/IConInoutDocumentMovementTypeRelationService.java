package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInoutDocumentMovementTypeRelation;

import java.util.List;

/**
 * 出入库作业类型&单据作业类型对照Service接口
 *
 * @author c
 * @date 2022-03-11
 */
public interface IConInoutDocumentMovementTypeRelationService extends IService<ConInoutDocumentMovementTypeRelation> {
    /**
     * 查询出入库作业类型&单据作业类型对照
     *
     * @param sid 出入库作业类型&单据作业类型对照ID
     * @return 出入库作业类型&单据作业类型对照
     */
    public ConInoutDocumentMovementTypeRelation selectConInoutDocumentMovementTypeRelationById(Long sid);

    /**
     * 查询出入库作业类型&单据作业类型对照列表
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 出入库作业类型&单据作业类型对照集合
     */
    public List<ConInoutDocumentMovementTypeRelation> selectConInoutDocumentMovementTypeRelationList(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 新增出入库作业类型&单据作业类型对照
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    public int insertConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 修改出入库作业类型&单据作业类型对照
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    public int updateConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 变更出入库作业类型&单据作业类型对照
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    public int changeConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 批量删除出入库作业类型&单据作业类型对照
     *
     * @param sids 需要删除的出入库作业类型&单据作业类型对照ID
     * @return 结果
     */
    public int deleteConInoutDocumentMovementTypeRelationByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conInoutDocumentMovementTypeRelation
     * @return
     */
    int changeStatus(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 更改确认状态
     *
     * @param conInoutDocumentMovementTypeRelation
     * @return
     */
    int check(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

}
