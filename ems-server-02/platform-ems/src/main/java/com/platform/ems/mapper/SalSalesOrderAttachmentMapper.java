package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesOrderAttachment;

/**
 * 销售订单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface SalSalesOrderAttachmentMapper  extends BaseMapper<SalSalesOrderAttachment> {


    SalSalesOrderAttachment selectSalSalesOrderAttachmentById(Long salesOrderAttachmentSid);

    List<SalSalesOrderAttachment> selectSalSalesOrderAttachmentList(SalSalesOrderAttachment salSalesOrderAttachment);

    /**
     * 添加多个
     * @param list List SalSalesOrderAttachment
     * @return int
     */
    int inserts(@Param("list") List<SalSalesOrderAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSalesOrderAttachment
    * @return int
    */
    int updateAllById(SalSalesOrderAttachment entity);

    /**
     * 更新多个
     * @param list List SalSalesOrderAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesOrderAttachment> list);


    void deleteSalSalesOrderAttachmentByIds(@Param("array")Long[] salesOrderSids);
}
