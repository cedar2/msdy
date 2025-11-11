package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.List;

import com.platform.ems.domain.dto.request.InvMaterialRequisitionReportRequest;
import com.platform.ems.domain.dto.response.InvMaterialRequisitionReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvMaterialRequisitionItem;

/**
 * 领退料单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface InvMaterialRequisitionItemMapper  extends BaseMapper<InvMaterialRequisitionItem> {


    List<InvMaterialRequisitionItem> selectInvMaterialRequisitionItemById(Long materialRequisitionSid);

    BigDecimal getQuantity(Long materialRequisitionItemSid);

    List<InvMaterialRequisitionItem> selectInvMaterialRequisitionItemList(InvMaterialRequisitionItem invMaterialRequisitionItem);

    /**
     * 获取领退料明细报表
     */
    List<InvMaterialRequisitionReportResponse> reportInvMaterialRequisition(InvMaterialRequisitionReportRequest invMaterialRequisitionReportRequest);

    /**
     * 添加多个
     * @param list List InvMaterialRequisitionItem
     * @return int
     */
    int inserts(@Param("list") List<InvMaterialRequisitionItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvMaterialRequisitionItem
    * @return int
    */
    int updateAllById(InvMaterialRequisitionItem entity);

    /**
     * 更新多个
     * @param list List InvMaterialRequisitionItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvMaterialRequisitionItem> list);


    void deleteInvMaterialRequisitionItemByIds(@Param("array")Long[] materialRequisitionSids);
}
