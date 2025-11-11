package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourceInquiry;
import com.platform.ems.domain.PurOutsourceQuoteBargain;

/**
 * 加工询价单主Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurOutsourceInquiryService extends IService<PurOutsourceInquiry> {
    /**
     * 查询加工询价单主
     *
     * @param outsourceInquirySid 加工询价单主ID
     * @return 加工询价单主
     */
    public PurOutsourceInquiry selectPurOutsourceInquiryById(Long outsourceInquirySid);

    /**
     * 查询加工询价单主列表
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 加工询价单主集合
     */
    public List<PurOutsourceInquiry> selectPurOutsourceInquiryList(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 新增加工询价单主
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    public int insertPurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 修改加工询价单主
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    public int updatePurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 变更加工询价单主
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    public int changePurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 批量删除加工询价单主
     *
     * @param outsourceInquirySids 需要删除的加工询价单主ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryByIds(List<Long> outsourceInquirySids);

    /**
     * 更改确认状态
     *
     * @param purOutsourceInquiry
     * @return
     */
    int check(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 推送
     *
     * @param outsourceInquirySids
     * @return
     */
    int sent(List<Long> outsourceInquirySids);

    /**
     * 查询加工物料询价单主-去报价-前的校验
     *
     * @param purOutsourceInquiry 加工物料询价单主ID 和 供应商id
     * @return
     */
    public Object checkQuote(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 查询物料询价单主-去报价
     *
     * @param purOutsourceInquiry 加工物料询价单主ID 和供应商sid
     * @return 加工物料报价单主
     */
    public PurOutsourceQuoteBargain toQuote(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 复制
     *
     * @param outsourceInquirySid 加工询价单主ID
     * @return 加工询价单主
     */
    public PurOutsourceInquiry copy(Long outsourceInquirySid);
}
