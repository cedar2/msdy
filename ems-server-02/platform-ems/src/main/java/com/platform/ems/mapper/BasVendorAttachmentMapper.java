package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorAttachment;

/**
 * 供应商-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-13
 */
public interface BasVendorAttachmentMapper  extends BaseMapper<BasVendorAttachment> {


    BasVendorAttachment selectBasVendorAttachmentById(Long vendorAttachmentSid);

    List<BasVendorAttachment> selectBasVendorAttachmentList(BasVendorAttachment basVendorAttachment);

    /**
     * 添加多个
     * @param list List BasVendorAttachment
     * @return int
     */
    int inserts(@Param("list") List<BasVendorAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasVendorAttachment
     * @return int
     */
    int updateAllById(BasVendorAttachment entity);

    /**
     * 更新多个
     * @param list List BasVendorAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorAttachment> list);


}