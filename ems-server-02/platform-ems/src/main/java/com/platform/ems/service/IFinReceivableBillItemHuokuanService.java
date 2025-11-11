package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemHuokuan;

/**
 * 收款单-核销货款明细表Service接口
 */
public interface IFinReceivableBillItemHuokuanService extends IService<FinReceivableBillItemHuokuan>{

    /**
     * 查询收款单-核销货款明细表
     *
     * @param receivableBillItemHuokuanSid 收款单-核销货款明细表ID
     * @return 收款单-核销货款明细表
     */
    public FinReceivableBillItemHuokuan selectFinReceivableBillItemHuokuanById(Long receivableBillItemHuokuanSid);

    /**
     * 查询收款单-核销货款明细表列表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 收款单-核销货款明细表集合
     */
    public List<FinReceivableBillItemHuokuan> selectFinReceivableBillItemHuokuanList(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan);

    /**
     * 新增收款单-核销货款明细表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan);

    /**
     * 修改收款单-核销货款明细表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan);

    /**
     * 变更收款单-核销货款明细表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan);

    /**
     * 批量删除收款单-核销货款明细表
     *
     * @param receivableBillItemHuokuanSids 需要删除的收款单-核销货款明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemHuokuanByIds(List<Long>  receivableBillItemHuokuanSids);

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
    public int deleteByList(List<FinReceivableBillItemHuokuan> itemList);
}
