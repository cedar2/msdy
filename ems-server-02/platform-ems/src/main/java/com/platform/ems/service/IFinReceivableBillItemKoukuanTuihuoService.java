package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemKoukuanTuihuo;

/**
 * 收款单-核销退货扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinReceivableBillItemKoukuanTuihuoService extends IService<FinReceivableBillItemKoukuanTuihuo>{

    /**
     * 查询收款单-核销退货扣款明细表
     *
     * @param receivableBillItemKoukuanTuihuoSid 收款单-核销退货扣款明细表ID
     * @return 收款单-核销退货扣款明细表
     */
    public FinReceivableBillItemKoukuanTuihuo selectFinReceivableBillItemKoukuanTuihuoById(Long receivableBillItemKoukuanTuihuoSid);

    /**
     * 查询收款单-核销退货扣款明细表列表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 收款单-核销退货扣款明细表集合
     */
    public List<FinReceivableBillItemKoukuanTuihuo> selectFinReceivableBillItemKoukuanTuihuoList(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo);

    /**
     * 新增收款单-核销退货扣款明细表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo);

    /**
     * 修改收款单-核销退货扣款明细表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo);

    /**
     * 变更收款单-核销退货扣款明细表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo);

    /**
     * 批量删除收款单-核销退货扣款明细表
     *
     * @param receivableBillItemKoukuanTuihuoSids 需要删除的收款单-核销退货扣款明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemKoukuanTuihuoByIds(List<Long>  receivableBillItemKoukuanTuihuoSids);

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
    public int deleteByList(List<FinReceivableBillItemKoukuanTuihuo> itemList);
}
