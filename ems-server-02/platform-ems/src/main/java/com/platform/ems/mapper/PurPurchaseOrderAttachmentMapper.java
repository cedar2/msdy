package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderAttachment;

/**
 * 采购订单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface PurPurchaseOrderAttachmentMapper  extends BaseMapper<PurPurchaseOrderAttachment> {


    PurPurchaseOrderAttachment selectPurPurchaseOrderAttachmentById(Long purchaseOrderAttachmentSid);

    List<PurPurchaseOrderAttachment> selectPurPurchaseOrderAttachmentList(PurPurchaseOrderAttachment purPurchaseOrderAttachment);

    /**
     * 添加多个
     * @param list List PurPurchaseOrderAttachment
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseOrderAttachment
    * @return int
    */
    int updateAllById(PurPurchaseOrderAttachment entity);

    /**
     * 更新多个
     * @param list List PurPurchaseOrderAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderAttachment> list);


    void deletePurPurchaseOrderAttachmentByIds(@Param("array")Long[] purchaseOrderSids);
}
