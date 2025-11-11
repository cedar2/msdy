package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinSaleDeductionBillAttachment;

/**
 * 销售扣款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface FinSaleDeductionBillAttachmentMapper  extends BaseMapper<FinSaleDeductionBillAttachment> {


    FinSaleDeductionBillAttachment selectFinSaleDeductionAttachmentById(Long saleDeductionAttachmentSid);

    List<FinSaleDeductionBillAttachment> selectFinSaleDeductionAttachmentList(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment);

    /**
     * 添加多个
     * @param list List FinSaleDeductionBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinSaleDeductionBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleDeductionBillAttachment
    * @return int
    */
    int updateAllById(FinSaleDeductionBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinSaleDeductionBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleDeductionBillAttachment> list);


    void deleteFinSaleDeductionAttachmentByIds(@Param("array") Long[] saleDeductionSids);
}
