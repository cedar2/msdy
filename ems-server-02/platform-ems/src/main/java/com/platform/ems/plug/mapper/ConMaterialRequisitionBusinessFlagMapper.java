package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConMaterialRequisitionBusinessFlag;
import com.platform.ems.plug.service.IConMaterialRequisitionBusinessFlagService;

/**
 * 业务标识_领退料Mapper接口
 *
 * @author platform
 * @date 2024-11-10
 */
public interface ConMaterialRequisitionBusinessFlagMapper  extends BaseMapper<ConMaterialRequisitionBusinessFlag> {

    /**
     * 查询详情
     * @param sid 单据sid
     * @return ConMaterialRequisitionBusinessFlag
     */
        ConMaterialRequisitionBusinessFlag selectConMaterialRequisitionBusinessFlagById(Long sid);

    /**
     * 查询列表
     * @param conMaterialRequisitionBusinessFlag ConMaterialRequisitionBusinessFlag
     * @return List
     */
    List<ConMaterialRequisitionBusinessFlag> selectConMaterialRequisitionBusinessFlagList(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 添加多个
     * @param list List ConMaterialRequisitionBusinessFlag
     * @return int
     */
    int inserts(@Param("list") List<ConMaterialRequisitionBusinessFlag> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConMaterialRequisitionBusinessFlag
     * @return int
     */
    int updateAllById(ConMaterialRequisitionBusinessFlag entity);

    /**
     * 更新多个
     * @param list List ConMaterialRequisitionBusinessFlag
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMaterialRequisitionBusinessFlag> list);

}
