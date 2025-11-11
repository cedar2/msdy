package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemKoukuanKegongliao;

/**
 * 收款单-核销客供料扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinReceivableBillItemKoukuanKegongliaoService extends IService<FinReceivableBillItemKoukuanKegongliao> {

    /**
     * 查询收款单-核销客供料扣款明细表
     *
     * @param receivableBillItemKoukuanKegongliaoSid 收款单-核销客供料扣款明细表ID
     * @return 收款单-核销客供料扣款明细表
     */
    public FinReceivableBillItemKoukuanKegongliao selectFinReceivableBillItemKoukuanKegongliaoById(Long receivableBillItemKoukuanKegongliaoSid);

    /**
     * 查询收款单-核销客供料扣款明细表列表
     *
     * @param finReceivableBillItemKoukuanKegongliao 收款单-核销客供料扣款明细表
     * @return 收款单-核销客供料扣款明细表集合
     */
    public List<FinReceivableBillItemKoukuanKegongliao> selectFinReceivableBillItemKoukuanKegongliaoList(FinReceivableBillItemKoukuanKegongliao finReceivableBillItemKoukuanKegongliao);

    /**
     * 新增收款单-核销客供料扣款明细表
     *
     * @param finReceivableBillItemKoukuanKegongliao 收款单-核销客供料扣款明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemKoukuanKegongliao(FinReceivableBillItemKoukuanKegongliao finReceivableBillItemKoukuanKegongliao);

    /**
     * 修改收款单-核销客供料扣款明细表
     *
     * @param finReceivableBillItemKoukuanKegongliao 收款单-核销客供料扣款明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemKoukuanKegongliao(FinReceivableBillItemKoukuanKegongliao finReceivableBillItemKoukuanKegongliao);

    /**
     * 变更收款单-核销客供料扣款明细表
     *
     * @param finReceivableBillItemKoukuanKegongliao 收款单-核销客供料扣款明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemKoukuanKegongliao(FinReceivableBillItemKoukuanKegongliao finReceivableBillItemKoukuanKegongliao);

    /**
     * 批量删除收款单-核销客供料扣款明细表
     *
     * @param receivableBillItemKoukuanKegongliaoSids 需要删除的收款单-核销客供料扣款明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemKoukuanKegongliaoByIds(List<Long> receivableBillItemKoukuanKegongliaoSids);

    /**
     * 批量新增
     */
    public int insertByList(FinReceivableBill bill);

    /**
     * 批量修改
     */
    public int updateByList(FinReceivableBill bill);

    /**
     * 批量删除
     */
    public int deleteByList(List<FinReceivableBillItemKoukuanKegongliao> itemList);
}
