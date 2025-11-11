package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PurPurchaseContractAttachment;
import org.apache.ibatis.annotations.Param;

/**
 * 采购合同信息-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface PurPurchaseContractAttachmentMapper extends BaseMapper<PurPurchaseContractAttachment> {

    PurPurchaseContractAttachment selectPurPurchaseContractAttachmentById(Long purchaseContractAttachmentSid);

    List<PurPurchaseContractAttachment> selectPurPurchaseContractAttachmentList(PurPurchaseContractAttachment purPurchaseContractAttachm);

    /**
     * 添加多个
     *
     * @param list List PurPurchaseContractAttachm
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseContractAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurPurchaseContractAttachm
     * @return int
     */
    int updateAllById(PurPurchaseContractAttachment entity);

    /**
     * 更新多个
     *
     * @param list List PurPurchaseContractAttachm
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseContractAttachment> list);


    void deletePurPurchaseContractAttachmentByIds(@Param("list") List<Long> purchaseContractSids);
}
