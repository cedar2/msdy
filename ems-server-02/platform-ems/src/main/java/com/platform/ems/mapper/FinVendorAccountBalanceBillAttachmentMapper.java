package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorAccountBalanceBillAttachment;

/**
 * 供应商账互抵单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface FinVendorAccountBalanceBillAttachmentMapper  extends BaseMapper<FinVendorAccountBalanceBillAttachment> {


    FinVendorAccountBalanceBillAttachment selectFinVendorAccountBalanceBillAttachmentById(Long vendorAccountBalanceBillAttachmentSid);

    List<FinVendorAccountBalanceBillAttachment> selectFinVendorAccountBalanceBillAttachmentList(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 添加多个
     * @param list List FinVendorAccountBalanceBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountBalanceBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountBalanceBillAttachment
    * @return int
    */
    int updateAllById(FinVendorAccountBalanceBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountBalanceBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountBalanceBillAttachment> list);


}
