package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillAttachment;

/**
 * 收款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-22
 */
public interface FinReceivableBillAttachmentMapper  extends BaseMapper<FinReceivableBillAttachment> {


    FinReceivableBillAttachment selectFinReceivableBillAttachmentById(Long recervableBillAttachmentSid);

    List<FinReceivableBillAttachment> selectFinReceivableBillAttachmentList(FinReceivableBillAttachment finReceivableBillAttachment);

    /**
     * 添加多个
     * @param list List FinReceivableBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinReceivableBillAttachment
    * @return int
    */
    int updateAllById(FinReceivableBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinReceivableBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillAttachment> list);

    void deleteFinReceivableBillAttachmentByIds(@Param("array") Long[] recervableBillSids);
}
