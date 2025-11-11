package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchaseOrderAttachment;

/**
 * 外发加工单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface PurOutsourcePurchaseOrderAttachmentMapper  extends BaseMapper<PurOutsourcePurchaseOrderAttachment> {


    PurOutsourcePurchaseOrderAttachment selectPurOutsourcePurchaseOrderAttachmentById(Long outsourcePurchaseOrderAttachmentSid);

    List<PurOutsourcePurchaseOrderAttachment> selectPurOutsourcePurchaseOrderAttachmentList(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

    /**
     * 添加多个
     * @param list List PurOutsourcePurchaseOrderAttachment
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchaseOrderAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchaseOrderAttachment
    * @return int
    */
    int updateAllById(PurOutsourcePurchaseOrderAttachment entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchaseOrderAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchaseOrderAttachment> list);


    void deleteOutsourcePurchaseOrderAttachmentByIds(@Param("list") List<Long> outsourcePurchaseOrderSids);
}
