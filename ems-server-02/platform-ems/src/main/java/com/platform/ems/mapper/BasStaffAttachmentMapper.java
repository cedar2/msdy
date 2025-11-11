package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasStaffAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-10-12
 */
public interface BasStaffAttachmentMapper extends BaseMapper<BasStaffAttachment>  {

    BasStaffAttachment selectBasStaffAttachmentById(Long staffAttachmentSid);

    List<BasStaffAttachment> selectBasStaffAttachmentList(BasStaffAttachment basStaffAttachment);

    /**
     * 添加多个
     * @param list List BasStaffAttachment
     * @return int
     */
    int inserts(@Param("list") List<BasStaffAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasStaffAttachment
     * @return int
     */
    int updateAllById(BasStaffAttachment entity);

    /**
     * 更新多个
     * @param list List BasStaffAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStaffAttachment> list);
}
