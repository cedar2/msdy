package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PurInquiryItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurInquiryVendor;

/**
 * 物料询价单-供应商Mapper接口
 *
 * @author chenkw
 * @date 2022-03-21
 */
public interface PurInquiryVendorMapper extends BaseMapper<PurInquiryVendor> {


    PurInquiryVendor selectPurInquiryVendorById(Long inquiryVendorSid);

    List<PurInquiryVendor> selectPurInquiryVendorList(PurInquiryVendor purInquiryVendor);

    /**
     * 添加多个
     *
     * @param list List PurInquiryVendor
     * @return int
     */
    int inserts(@Param("list") List<PurInquiryVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurInquiryVendor
     * @return int
     */
    int updateAllById(PurInquiryVendor entity);

    /**
     * 更新多个
     *
     * @param list List PurInquiryVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<PurInquiryVendor> list);

    /**
     * 通过主表sid查主表下供应商明细
     * @param inquirySid Long inquirySid
     * @return int
     */
    List<PurInquiryVendor> selectPurInquiryVendorListById(Long inquirySid);

    /**
     * 通过批量主表sid批量删除供应商明细
     * @param inquirySidList Long inquirySidList
     * @return int
     */
    int deletePurInquiryVendorByInquirySids(@Param("inquirySid") List<Long> inquirySidList);

    /**
     * 通过批量主表sid查询所有供应商明细表SID
     * @param inquirySid Long inquirySid
     * @return int
     */
    List<Long> selectPurInquiryVendorSidListById(@Param("inquirySid") Long[] inquirySid);


}
