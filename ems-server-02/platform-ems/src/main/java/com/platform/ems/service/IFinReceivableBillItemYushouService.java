package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemYushou;

/**
 * 收款单-核销预收明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinReceivableBillItemYushouService extends IService<FinReceivableBillItemYushou>{

    /**
     * 查询收款单-核销预收明细表
     *
     * @param receivableBillItemYushouSid 收款单-核销预收明细表ID
     * @return 收款单-核销预收明细表
     */
    public FinReceivableBillItemYushou selectFinReceivableBillItemYushouById(Long receivableBillItemYushouSid);

    /**
     * 查询收款单-核销预收明细表列表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 收款单-核销预收明细表集合
     */
    public List<FinReceivableBillItemYushou> selectFinReceivableBillItemYushouList(FinReceivableBillItemYushou finReceivableBillItemYushou);

    /**
     * 新增收款单-核销预收明细表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou);

    /**
     * 修改收款单-核销预收明细表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou);

    /**
     * 变更收款单-核销预收明细表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou);

    /**
     * 批量删除收款单-核销预收明细表
     *
     * @param receivableBillItemYushouSids 需要删除的收款单-核销预收明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemYushouByIds(List<Long>  receivableBillItemYushouSids);

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
    public int deleteByList(List<FinReceivableBillItemYushou> itemList);
}
