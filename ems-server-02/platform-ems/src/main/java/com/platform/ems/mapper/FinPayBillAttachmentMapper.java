package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillAttachment;

/**
 * 付款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface FinPayBillAttachmentMapper  extends BaseMapper<FinPayBillAttachment> {


    FinPayBillAttachment selectFinPayBillAttachmentById(Long payBillAttachmentSid);

    List<FinPayBillAttachment> selectFinPayBillAttachmentList(FinPayBillAttachment finPayBillAttachment);

    /**
     * 添加多个
     * @param list List FinPayBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPayBillAttachment
    * @return int
    */
    int updateAllById(FinPayBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinPayBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillAttachment> list);


    void deleteFinPayBillAttachmentByIds(@Param("array") Long[] payBillSids);
}
