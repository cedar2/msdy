package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourceInquiryAttach;

/**
 * 加工询价单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurOutsourceInquiryAttachMapper extends BaseMapper<PurOutsourceInquiryAttach> {


    PurOutsourceInquiryAttach selectPurOutsourceInquiryAttachById(Long outsourceInquiryAttachmentSid);

    List<PurOutsourceInquiryAttach> selectPurOutsourceInquiryAttachList(PurOutsourceInquiryAttach purOutsourceInquiryAttach);

    /**
     * 添加多个
     *
     * @param list List PurOutsourceInquiryAttach
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceInquiryAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurOutsourceInquiryAttach
     * @return int
     */
    int updateAllById(PurOutsourceInquiryAttach entity);

    /**
     * 更新多个
     *
     * @param list List PurOutsourceInquiryAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceInquiryAttach> list);


}
