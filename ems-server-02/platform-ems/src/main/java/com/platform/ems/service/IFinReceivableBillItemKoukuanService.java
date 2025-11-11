package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemKoukuan;

/**
 * 收款单-核销扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinReceivableBillItemKoukuanService extends IService<FinReceivableBillItemKoukuan>{

    /**
     * 查询收款单-核销扣款明细表
     *
     * @param receivableBillItemKoukuanSid 收款单-核销扣款明细表ID
     * @return 收款单-核销扣款明细表
     */
    public FinReceivableBillItemKoukuan selectFinReceivableBillItemKoukuanById(Long receivableBillItemKoukuanSid);

    /**
     * 查询收款单-核销扣款明细表列表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 收款单-核销扣款明细表集合
     */
    public List<FinReceivableBillItemKoukuan> selectFinReceivableBillItemKoukuanList(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan);

    /**
     * 新增收款单-核销扣款明细表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan);

    /**
     * 修改收款单-核销扣款明细表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan);

    /**
     * 变更收款单-核销扣款明细表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan);

    /**
     * 批量删除收款单-核销扣款明细表
     *
     * @param receivableBillItemKoukuanSids 需要删除的收款单-核销扣款明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemKoukuanByIds(List<Long>  receivableBillItemKoukuanSids);

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
    public int deleteByList(List<FinReceivableBillItemKoukuan> itemList);
}
