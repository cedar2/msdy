package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPurchaseDeductionBillAttachment;

/**
 * 采购扣款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface FinPurchaseDeductionBillAttachmentMapper  extends BaseMapper<FinPurchaseDeductionBillAttachment> {


    FinPurchaseDeductionBillAttachment selectFinPurchaseDeductionAttachmentById(Long purchaseDeductionAttachmentSid);

    List<FinPurchaseDeductionBillAttachment> selectFinPurchaseDeductionAttachmentList(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment);

    /**
     * 添加多个
     * @param list List FinPurchaseDeductionBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseDeductionBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseDeductionBillAttachment
    * @return int
    */
    int updateAllById(FinPurchaseDeductionBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinPurchaseDeductionBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseDeductionBillAttachment> list);


    void deleteFinPurchaseDeductionAttachmentByIds(@Param("array")Long[] purchaseDeductionSids);
}
