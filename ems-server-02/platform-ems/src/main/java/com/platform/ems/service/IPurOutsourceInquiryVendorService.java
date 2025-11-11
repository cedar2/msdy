package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.*;

/**
 * 加工询价单-供应商Service接口
 *
 * @author chenkw
 * @date 2022-03-21
 */
public interface IPurOutsourceInquiryVendorService extends IService<PurOutsourceInquiryVendor>{
    /**
     * 查询加工询价单-供应商
     *
     * @param outsourceInquiryVendorSid 加工询价单-供应商ID
     * @return 加工询价单-供应商
     */
    public PurOutsourceInquiryVendor selectPurOutsourceInquiryVendorById(Long outsourceInquiryVendorSid);

    /**
     * 查询加工询价单-供应商列表
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 加工询价单-供应商集合
     */
    public List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorList(PurOutsourceInquiryVendor purOutsourceInquiryVendor);

    /**
     * 新增加工询价单-供应商
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    public int insertPurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor);

    /**
     * 修改加工询价单-供应商
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    public int updatePurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor);

    /**
     * 变更加工询价单-供应商
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    public int changePurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor);

    /**
     * 批量删除加工询价单-供应商
     *
     * @param outsourceInquiryVendorSids 需要删除的加工询价单-供应商ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryVendorByIds(List<Long>  outsourceInquiryVendorSids);

    /**
     * 根据主表id查询加工询价单供应商明细列表
     *
     * @param outsourceInquirySid 加工询价单ID
     * @return 加工询价单明细集合
     */
    public List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorListById(Long outsourceInquirySid);

    /**
     * 批量新增加工询价单供应商明细
     *
     * @param list 加工询价单供应商明细
     * @return 结果
     */
    public int insertPurOutsourceInquiryVendorList(List<PurOutsourceInquiryVendor> list, PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 批量删除加工询价单供应商明细
     *
     * @param outsourceInquirySids 需要删除的加工询价单ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryVendorByInquirySids(List<Long> outsourceInquirySids);

    /**
     * 根据主表id查询加工询价单供应商明细SID列表
     *
     * @param outsourceInquirySids 加工询价单ID
     * @return 加工询价单明细集合
     */
    List<Long> selectPurOutsourceInquiryVendorSidListById(Long[] outsourceInquirySids);

    /**
     * 批量修改加工询价单供应商明细
     *
     * @param list 加工询价单供应商明细
     * @return 结果
     */
    public int updatePurOutsourceInquiryVendorList(List<PurOutsourceInquiryVendor> list, PurOutsourceInquiry purOutsourceInquiry);
}
