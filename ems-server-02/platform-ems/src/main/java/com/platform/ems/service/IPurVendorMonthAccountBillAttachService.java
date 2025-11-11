package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurVendorMonthAccountBillAttach;

import java.util.List;

/**
 * 供应商对账单-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IPurVendorMonthAccountBillAttachService extends IService<PurVendorMonthAccountBillAttach> {
    /**
     * 查询供应商对账单-附件
     *
     * @param monthAccountBillAttachmentSid 供应商对账单-附件ID
     * @return 供应商对账单-附件
     */
    public PurVendorMonthAccountBillAttach selectPurVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    /**
     * 查询供应商对账单-附件列表
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 供应商对账单-附件集合
     */
    public List<PurVendorMonthAccountBillAttach> selectPurVendorMonthAccountBillAttachList(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach);

    /**
     * 新增供应商对账单-附件
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    public int insertPurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach);

    /**
     * 修改供应商对账单-附件
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    public int updatePurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach);

    /**
     * 变更供应商对账单-附件
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    public int changePurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach);

    /**
     * 批量删除供应商对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的供应商对账单-附件ID
     * @return 结果
     */
    public int deletePurVendorMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids);

}
