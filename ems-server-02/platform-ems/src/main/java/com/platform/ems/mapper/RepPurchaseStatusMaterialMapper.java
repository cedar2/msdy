package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepPurchaseStatusMaterial;

/**
 * 采购状况-面辅料/商品Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepPurchaseStatusMaterialMapper extends BaseMapper<RepPurchaseStatusMaterial> {


    RepPurchaseStatusMaterial selectRepPurchaseStatusMaterialById(Long dataRecordSid);

    List<RepPurchaseStatusMaterial> selectRepPurchaseStatusMaterialList(RepPurchaseStatusMaterial repPurchaseStatusMaterial);

    /**
     * 添加多个
     *
     * @param list List RepPurchaseStatusMaterial
     * @return int
     */
    int inserts(@Param("list") List<RepPurchaseStatusMaterial> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepPurchaseStatusMaterial
     * @return int
     */
    int updateAllById(RepPurchaseStatusMaterial entity);

    /**
     * 更新多个
     *
     * @param list List RepPurchaseStatusMaterial
     * @return int
     */
    int updatesAllById(@Param("list") List<RepPurchaseStatusMaterial> list);


}
