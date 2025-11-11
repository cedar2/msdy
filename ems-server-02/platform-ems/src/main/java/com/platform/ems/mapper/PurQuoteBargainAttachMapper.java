package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.PurQuoteBargainAttach;
import org.apache.ibatis.annotations.Param;

/**
 * 询报议价单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurQuoteBargainAttachMapper extends BaseMapper<PurQuoteBargainAttach> {


    PurQuoteBargainAttach selectPurRequestQuotationAttachmentById(Long requestQuotationAttachmentSid);

    List<PurQuoteBargainAttach> selectPurRequestQuotationAttachmentList(PurQuoteBargainAttach purQuoteBargainAttach);

    /**
     * 添加多个
     * @param list List PurQuoteBargainAttach
     * @return int
     */
    int inserts(@Param("list") List<PurQuoteBargainAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurQuoteBargainAttach
    * @return int
    */
    int updateAllById(PurQuoteBargainAttach entity);

    /**
     * 更新多个
     * @param list List PurQuoteBargainAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PurQuoteBargainAttach> list);


}
