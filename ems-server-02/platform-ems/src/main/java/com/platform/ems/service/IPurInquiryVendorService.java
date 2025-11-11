package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.domain.PurInquiryItem;
import com.platform.ems.domain.PurInquiryVendor;

/**
 * 物料询价单-供应商Service接口
 *
 * @author chenkw
 * @date 2022-03-21
 */
public interface IPurInquiryVendorService extends IService<PurInquiryVendor> {
    /**
     * 查询物料询价单-供应商
     *
     * @param inquiryVendorSid 物料询价单-供应商ID
     * @return 物料询价单-供应商
     */
    public PurInquiryVendor selectPurInquiryVendorById(Long inquiryVendorSid);

    /**
     * 查询物料询价单-供应商列表
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 物料询价单-供应商集合
     */
    public List<PurInquiryVendor> selectPurInquiryVendorList(PurInquiryVendor purInquiryVendor);

    /**
     * 新增物料询价单-供应商
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    public int insertPurInquiryVendor(PurInquiryVendor purInquiryVendor);

    /**
     * 修改物料询价单-供应商
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    public int updatePurInquiryVendor(PurInquiryVendor purInquiryVendor);

    /**
     * 变更物料询价单-供应商
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    public int changePurInquiryVendor(PurInquiryVendor purInquiryVendor);

    /**
     * 批量删除物料询价单-供应商
     *
     * @param inquiryVendorSids 需要删除的物料询价单-供应商ID
     * @return 结果
     */
    public int deletePurInquiryVendorByIds(List<Long> inquiryVendorSids);

    /**
     * 根据主表id查询物料询价单供应商明细列表
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细集合
     */
    public List<PurInquiryVendor> selectPurInquiryVendorListById(Long inquirySid);

    /**
     * 批量新增物料询价单供应商明细
     *
     * @param list 物料询价单供应商明细
     * @return 结果
     */
    public int insertPurInquiryVendorList(List<PurInquiryVendor> list, PurInquiry purInquiry);

    /**
     * 批量删除采购询价单供应商明细
     *
     * @param inquirySids 需要删除的采购询价单ID
     * @return 结果
     */
    public int deletePurInquiryVendorByInquirySids(List<Long> inquirySids);

    /**
     * 根据主表id查询采购询价单供应商明细SID列表
     *
     * @param inquirySids 采购询价单ID
     * @return 采购询价单明细集合
     */
    List<Long> selectPurInquiryVendorSidListById(Long[] inquirySids);

    /**
     * 批量修改物料询价单明细 供应商
     *
     * @param list 物料询价单供应商明细
     * @return 结果
     */
    public int updatePurInquiryVendorList(List<PurInquiryVendor> list, PurInquiry purInquiry);

}
