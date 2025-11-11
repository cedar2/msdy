package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSaleContractAttachment;

/**
 * 销售合同信息-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-18
 */
public interface SalSaleContractAttachmentMapper  extends BaseMapper<SalSaleContractAttachment> {


    SalSaleContractAttachment selectSalSaleContractAttachmentById(Long saleContractAttachmentSid);

    List<SalSaleContractAttachment> selectSalSaleContractAttachmentList(SalSaleContractAttachment salSaleContractAttachment);

    /**
     * 添加多个
     * @param list List SalSaleContractAttachment
     * @return int
     */
    int inserts(@Param("list") List<SalSaleContractAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSaleContractAttachment
    * @return int
    */
    int updateAllById(SalSaleContractAttachment entity);

    /**
     * 更新多个
     * @param list List SalSaleContractAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSaleContractAttachment> list);


    void deleteSalSaleContractAttachmentByIds(@Param("list") List<Long> saleContractSids);
}
