package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PurOutsourceInquiryItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourceInquiryVendor;

/**
 * 加工询价单-供应商Mapper接口
 *
 * @author chenkw
 * @date 2022-03-21
 */
public interface PurOutsourceInquiryVendorMapper extends BaseMapper<PurOutsourceInquiryVendor> {


    PurOutsourceInquiryVendor selectPurOutsourceInquiryVendorById(Long outsourceInquiryVendorSid);

    List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorList(PurOutsourceInquiryVendor purOutsourceInquiryVendor);

    /**
     * 添加多个
     *
     * @param list List PurOutsourceInquiryVendor
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceInquiryVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurOutsourceInquiryVendor
     * @return int
     */
    int updateAllById(PurOutsourceInquiryVendor entity);

    /**
     * 更新多个
     *
     * @param list List PurOutsourceInquiryVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceInquiryVendor> list);


    /**
     * 通过主表sid查主表下供应商明细
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorListById(Long outsourceInquirySid);

    /**
     * 通过批量主表sid批量删除供应商明细
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    int deletePurOutsourceInquiryVendorByInquirySids(@Param("outsourceInquirySid") List<Long> outsourceInquirySid);

    /**
     * 通过批量主表sid查询所有供应商明细表SID
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    List<Long> selectPurOutsourceInquiryVendorSidListById(@Param("outsourceInquirySid") Long[] outsourceInquirySid);


}
