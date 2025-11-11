package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurInquiryAttach;

/**
 * 物料询价单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurInquiryAttachMapper extends BaseMapper<PurInquiryAttach> {


    PurInquiryAttach selectPurInquiryAttachById(Long inquiryAttachmentSid);

    List<PurInquiryAttach> selectPurInquiryAttachList(PurInquiryAttach purInquiryAttach);

    /**
     * 添加多个
     *
     * @param list List PurInquiryAttach
     * @return int
     */
    int inserts(@Param("list") List<PurInquiryAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurInquiryAttach
     * @return int
     */
    int updateAllById(PurInquiryAttach entity);

    /**
     * 更新多个
     *
     * @param list List PurInquiryAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PurInquiryAttach> list);


}
