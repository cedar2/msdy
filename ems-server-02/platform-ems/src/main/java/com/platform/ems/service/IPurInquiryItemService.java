package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.domain.PurInquiryItem;

/**
 * 物料询价单明细Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurInquiryItemService extends IService<PurInquiryItem> {
    /**
     * 查询物料询价单明细
     *
     * @param inquiryItemSid 物料询价单明细ID
     * @return 物料询价单明细
     */
    public PurInquiryItem selectPurInquiryItemById(Long inquiryItemSid);

    /**
     * 查询物料询价单明细列表
     *
     * @param purInquiryItem 物料询价单明细
     * @return 物料询价单明细集合
     */
    public List<PurInquiryItem> selectPurInquiryItemList(PurInquiryItem purInquiryItem);

    /**
     * 新增物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    public int insertPurInquiryItem(PurInquiryItem purInquiryItem);

    /**
     * 修改物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    public int updatePurInquiryItem(PurInquiryItem purInquiryItem);

    /**
     * 变更物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    public int changePurInquiryItem(PurInquiryItem purInquiryItem);

    /**
     * 批量删除物料询价单明细
     *
     * @param inquiryItemSids 需要删除的物料询价单明细ID
     * @return 结果
     */
    public int deletePurInquiryItemByIds(List<Long> inquiryItemSids);

    /************************** new **************************/

    /**
     * 根据主表id查询物料询价单明细列表
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细集合
     */
    public List<PurInquiryItem> selectPurInquiryItemListById(Long inquirySid);

    /**
     * 批量新增物料询价单明细
     *
     * @param list 物料询价单明细
     * @return 结果
     */
    public int insertPurInquiryItemList(List<PurInquiryItem> list, PurInquiry purInquiry);

    /**
     * 批量修改物料询价单明细
     *
     * @param list 物料询价单明细
     * @return 结果
     */
    public int updatePurInquiryItemList(List<PurInquiryItem> list, PurInquiry purInquiry);

    /**
     * 批量删除物料询价单明细
     *
     * @param inquirySids 需要删除的物料询价单明细ID
     * @return 结果
     */
    public int deletePurInquiryItemByInquirySids(List<Long> inquirySids);

    /**
     * 根据主表id查询物料询价单明细SID列表
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细集合
     */
    List<Long> selectPurInquiryItemSidListById(Long[] inquirySid);

    /**
     * 查询物料询价单明细报表
     *
     * @param purInquiryItem 物料询价单明细
     * @return 物料询价单明细集合
     */
    public List<PurInquiryItem> getReportForm(PurInquiryItem purInquiryItem);

}
