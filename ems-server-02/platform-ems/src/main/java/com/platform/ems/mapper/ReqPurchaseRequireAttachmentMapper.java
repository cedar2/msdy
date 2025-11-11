package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ReqPurchaseRequireAttachment;

/**
 * 申购单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ReqPurchaseRequireAttachmentMapper  extends BaseMapper<ReqPurchaseRequireAttachment> {


    ReqPurchaseRequireAttachment selectReqPurchaseRequireAttachmentById(Long purchaseRequireAttachmentSid);

    List<ReqPurchaseRequireAttachment> selectReqPurchaseRequireAttachmentList(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment);

    /**
     * 添加多个
     * @param list List ReqPurchaseRequireAttachment
     * @return int
     */
    int inserts(@Param("list") List<ReqPurchaseRequireAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ReqPurchaseRequireAttachment
    * @return int
    */
    int updateAllById(ReqPurchaseRequireAttachment entity);

    /**
     * 更新多个
     * @param list List ReqPurchaseRequireAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqPurchaseRequireAttachment> list);


    void deleteReqPurchaseRequireAttachmentByIds(@Param("array")Long[] purchaseRequireSids);
}
