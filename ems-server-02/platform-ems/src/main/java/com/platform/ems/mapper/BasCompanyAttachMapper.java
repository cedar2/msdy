package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCompanyAttach;

/**
 * 公司档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface BasCompanyAttachMapper  extends BaseMapper<BasCompanyAttach> {


    BasCompanyAttach selectBasCompanyAttachById(Long attachmentSid);

    List<BasCompanyAttach> selectBasCompanyAttachList(BasCompanyAttach basCompanyAttach);

    /**
     * 添加多个
     * @param list List BasCompanyAttach
     * @return int
     */
    int inserts(@Param("list") List<BasCompanyAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasCompanyAttach
     * @return int
     */
    int updateAllById(BasCompanyAttach entity);

    /**
     * 更新多个
     * @param list List BasCompanyAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCompanyAttach> list);


}