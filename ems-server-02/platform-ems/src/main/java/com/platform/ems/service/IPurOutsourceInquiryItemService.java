package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.domain.PurInquiryItem;
import com.platform.ems.domain.PurOutsourceInquiry;
import com.platform.ems.domain.PurOutsourceInquiryItem;

/**
 * 加工询价单明细Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurOutsourceInquiryItemService extends IService<PurOutsourceInquiryItem> {
    /**
     * 查询加工询价单明细
     *
     * @param outsourceInquiryItemSid 加工询价单明细ID
     * @return 加工询价单明细
     */
    public PurOutsourceInquiryItem selectPurOutsourceInquiryItemById(Long outsourceInquiryItemSid);

    /**
     * 查询加工询价单明细列表
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 加工询价单明细集合
     */
    public List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemList(PurOutsourceInquiryItem purOutsourceInquiryItem);

    /**
     * 新增加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    public int insertPurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem);

    /**
     * 修改加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    public int updatePurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem);

    /**
     * 变更加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    public int changePurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem);

    /**
     * 批量删除加工询价单明细
     *
     * @param outsourceInquiryItemSids 需要删除的加工询价单明细ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryItemByIds(List<Long> outsourceInquiryItemSids);

    /************************** new **************************/

    /**
     * 根据主表id查询加工询价单明细列表
     *
     * @param outsourceInquirySid 加工询价单ID
     * @return 加工询价单明细集合
     */
    public List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemListById(Long outsourceInquirySid);

    /**
     * 批量新增加工询价单明细
     *
     * @param list 加工询价单明细
     * @return 结果
     */
    public int insertPurOutsourceInquiryItemList(List<PurOutsourceInquiryItem> list, PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 批量修改加工询价单明细
     *
     * @param list 加工询价单明细
     * @return 结果
     */
    public int updatePurOutsourceInquiryItemList(List<PurOutsourceInquiryItem> list, PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 批量删除加工询价单明细
     *
     * @param outsourceInquirySids 需要删除的加工询价单明细ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryItemByInquirySids(List<Long> outsourceInquirySids);

    /**
     * 根据主表id查询加工询价单明细SID列表
     *
     * @param outsourceInquirySids 加工询价单ID
     * @return 加工询价单明细集合
     */
    List<Long> selectPurOutsourceInquiryItemSidListById(Long[] outsourceInquirySids);

    /**
     * 查询加工询价单明细报表
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 加工询价单明细集合
     */
    public List<PurOutsourceInquiryItem> getReportForm(PurOutsourceInquiryItem purOutsourceInquiryItem);
}
