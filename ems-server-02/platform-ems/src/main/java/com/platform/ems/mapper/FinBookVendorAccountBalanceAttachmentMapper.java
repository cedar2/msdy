package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookVendorAccountBalanceAttachment;

/**
 * 财务流水账-附件-供应商账互抵Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-18
 */
public interface FinBookVendorAccountBalanceAttachmentMapper  extends BaseMapper<FinBookVendorAccountBalanceAttachment> {


    FinBookVendorAccountBalanceAttachment selectFinBookVendorAccountBalanceAttachmentById(Long bookAccountBalanceAttachmentSid);

    List<FinBookVendorAccountBalanceAttachment> selectFinBookVendorAccountBalanceAttachmentList(FinBookVendorAccountBalanceAttachment finBookVendorAccountBalanceAttachment);

    /**
     * 添加多个
     * @param list List FinBookVendorAccountBalanceAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountBalanceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorAccountBalanceAttachment
    * @return int
    */
    int updateAllById(FinBookVendorAccountBalanceAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookVendorAccountBalanceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountBalanceAttachment> list);


}
