package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.domain.PurQuoteBargain;

/**
 * 物料询价单主Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurInquiryService extends IService<PurInquiry> {
    /**
     * 查询物料询价单主
     *
     * @param inquirySid 物料询价单主ID
     * @return 物料询价单主
     */
    public PurInquiry selectPurInquiryById(Long inquirySid);

    /**
     * 查询物料询价单主列表
     *
     * @param purInquiry 物料询价单主
     * @return 物料询价单主集合
     */
    public List<PurInquiry> selectPurInquiryList(PurInquiry purInquiry);

    /**
     * 新增物料询价单主
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    public int insertPurInquiry(PurInquiry purInquiry);

    /**
     * 修改物料询价单主
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    public int updatePurInquiry(PurInquiry purInquiry);

    /**
     * 变更物料询价单主
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    public int changePurInquiry(PurInquiry purInquiry);

    /**
     * 批量删除物料询价单主
     *
     * @param inquirySids 需要删除的物料询价单主ID
     * @return 结果
     */
    public int deletePurInquiryByIds(List<Long> inquirySids);

    /**
     * 更改确认状态
     *
     * @param purInquiry
     * @return
     */
    int check(PurInquiry purInquiry);

    /**
     * 推送
     *
     * @param inquirySids
     * @return
     */
    int sent(List<Long> inquirySids);

    /**
     * 查询物料询价单主-去报价-前的校验
     *
     * @param purInquiry 物料询价单主ID 和 供应商id
     * @return
     */
    public Object checkQuote(PurInquiry purInquiry);

    /**
     * 查询物料询价单主-去报价
     *
     * @param purInquiry 物料询价单主ID 和 供应商id
     * @return 物料询价单主
     */
    public PurQuoteBargain toQuote(PurInquiry purInquiry);

    /**
     * 复制
     *
     * @param purInquirySid 物料询价单主ID
     * @return 物料询价单主
     */
    public PurInquiry copy(Long purInquirySid);

}
