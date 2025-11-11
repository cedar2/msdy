package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookVendorAccountAdjustAttachment;

/**
 * 财务流水账-附件-供应商调账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-02
 */
public interface FinBookVendorAccountAdjustAttachmentMapper  extends BaseMapper<FinBookVendorAccountAdjustAttachment> {


    FinBookVendorAccountAdjustAttachment selectFinBookVendorAccountAdjustAttachmentById(Long bookAccountAdjustAttachmentSid);

    List<FinBookVendorAccountAdjustAttachment> selectFinBookVendorAccountAdjustAttachmentList(FinBookVendorAccountAdjustAttachment finBookVendorAccountAdjustAttachment);

    /**
     * 添加多个
     * @param list List FinBookVendorAccountAdjustAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountAdjustAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorAccountAdjustAttachment
    * @return int
    */
    int updateAllById(FinBookVendorAccountAdjustAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookVendorAccountAdjustAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountAdjustAttachment> list);


}
