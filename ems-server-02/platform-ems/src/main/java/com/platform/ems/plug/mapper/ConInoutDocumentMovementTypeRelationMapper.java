package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConInoutDocumentMovementTypeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出入库作业类型&单据作业类型对照Mapper接口
 *
 * @author c
 * @date 2022-03-11
 */
public interface ConInoutDocumentMovementTypeRelationMapper extends BaseMapper<ConInoutDocumentMovementTypeRelation> {


    ConInoutDocumentMovementTypeRelation selectConInoutDocumentMovementTypeRelationById(Long sid);

    List<ConInoutDocumentMovementTypeRelation> selectConInoutDocumentMovementTypeRelationList(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation);

    /**
     * 添加多个
     *
     * @param list List ConInoutDocumentMovementTypeRelation
     * @return int
     */
    int inserts(@Param("list") List<ConInoutDocumentMovementTypeRelation> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConInoutDocumentMovementTypeRelation
     * @return int
     */
    int updateAllById(ConInoutDocumentMovementTypeRelation entity);

    /**
     * 更新多个
     *
     * @param list List ConInoutDocumentMovementTypeRelation
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInoutDocumentMovementTypeRelation> list);


}
