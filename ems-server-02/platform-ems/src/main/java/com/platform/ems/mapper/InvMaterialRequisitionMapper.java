package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.SalServiceAcceptance;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvMaterialRequisition;

/**
 * 领退料单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface InvMaterialRequisitionMapper  extends BaseMapper<InvMaterialRequisition> {


    InvMaterialRequisition selectInvMaterialRequisitionById(Long materialRequisitionSid);

    List<InvMaterialRequisition> selectInvMaterialRequisitionList(InvMaterialRequisition invMaterialRequisition);
    InvMaterialRequisition getName(Long materialRequisitionSid);
    /**
     * 添加多个
     * @param list List InvMaterialRequisition
     * @return int
     */
    int inserts(@Param("list") List<InvMaterialRequisition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvMaterialRequisition
    * @return int
    */
    int updateAllById(InvMaterialRequisition entity);

    /**
     * 更新多个
     * @param list List InvMaterialRequisition
     * @return int
     */
    int updatesAllById(@Param("list") List<InvMaterialRequisition> list);


    int countByDomain(InvMaterialRequisition params);

    int deleteInvMaterialRequisitionByIds(@Param("array")Long[] materialRequisitionSids);

    int confirm(InvMaterialRequisition invMaterialRequisition);
}
