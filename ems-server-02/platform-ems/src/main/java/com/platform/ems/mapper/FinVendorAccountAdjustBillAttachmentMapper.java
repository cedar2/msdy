package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinVendorAccountAdjustBillAttachment;

/**
 * 供应商调账单-附件Mapper接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface FinVendorAccountAdjustBillAttachmentMapper  extends BaseMapper<FinVendorAccountAdjustBillAttachment> {


    FinVendorAccountAdjustBillAttachment selectFinVendorAccountAdjustBillAttachmentById(Long adjustBillAttachmentSid);

    List<FinVendorAccountAdjustBillAttachment> selectFinVendorAccountAdjustBillAttachmentList(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 添加多个
     * @param list List FinVendorAccountAdjustBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountAdjustBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountAdjustBillAttachment
    * @return int
    */
    int updateAllById(FinVendorAccountAdjustBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountAdjustBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountAdjustBillAttachment> list);


}
