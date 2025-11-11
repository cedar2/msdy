package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchasePriceAttachment;

/**
 * 加工采购价-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-12
 */
public interface PurOutsourcePurchasePriceAttachmentMapper  extends BaseMapper<PurOutsourcePurchasePriceAttachment> {


    PurOutsourcePurchasePriceAttachment selectPurOutsourcePurchasePriceAttachmentById(Long outsourcePurchasePriceAttachmentSid);

    List<PurOutsourcePurchasePriceAttachment> selectPurOutsourcePurchasePriceAttachmentList(PurOutsourcePurchasePriceAttachment purOutsourcePurchasePriceAttachment);

    /**
     * 添加多个
     * @param list List PurOutsourcePurchasePriceAttachment
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchasePriceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchasePriceAttachment
    * @return int
    */
    int updateAllById(PurOutsourcePurchasePriceAttachment entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchasePriceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchasePriceAttachment> list);


}
