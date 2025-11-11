package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurVendorMonthAccountBillZangu;

import java.util.List;

/**
 * 供应商对账单-暂估明细Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IPurVendorMonthAccountBillZanguService extends IService<PurVendorMonthAccountBillZangu> {
    /**
     * 查询供应商对账单-暂估明细
     *
     * @param vendorMonthAccountBillZanguSid 供应商对账单-暂估明细ID
     * @return 供应商对账单-暂估明细
     */
    public PurVendorMonthAccountBillZangu selectPurVendorMonthAccountBillZanguById(Long vendorMonthAccountBillZanguSid);

    /**
     * 查询供应商对账单-暂估明细列表
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 供应商对账单-暂估明细集合
     */
    public List<PurVendorMonthAccountBillZangu> selectPurVendorMonthAccountBillZanguList(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach);

    /**
     * 新增供应商对账单-暂估明细
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    public int insertPurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach);

    /**
     * 修改供应商对账单-暂估明细
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    public int updatePurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach);

    /**
     * 变更供应商对账单-暂估明细
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    public int changePurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach);

    /**
     * 批量删除供应商对账单-暂估明细
     *
     * @param vendorMonthAccountBillZanguSids 需要删除的供应商对账单-暂估明细ID
     * @return 结果
     */
    public int deletePurVendorMonthAccountBillZanguByIds(List<Long> vendorMonthAccountBillZanguSids);

}
