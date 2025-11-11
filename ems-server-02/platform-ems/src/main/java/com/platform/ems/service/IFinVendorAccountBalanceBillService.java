package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountBalanceBill;
import com.platform.ems.domain.FinVendorAccountBalanceBillAttachment;
import com.platform.ems.domain.FinVendorAccountBalanceBillItem;

/**
 * 供应商账互抵单Service接口
 *
 * @author qhq
 * @date 2021-05-27
 */
public interface IFinVendorAccountBalanceBillService extends IService<FinVendorAccountBalanceBill>{
    /**
     * 查询供应商账互抵单
     *
     * @param vendorAccountBalanceBillSid 供应商账互抵单ID
     * @return 供应商账互抵单
     */
    public FinVendorAccountBalanceBill selectFinVendorAccountBalanceBillById(Long vendorAccountBalanceBillSid);

    /**
     * 查询供应商账互抵单列表
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 供应商账互抵单集合
     */
    public List<FinVendorAccountBalanceBill> selectFinVendorAccountBalanceBillList(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 新增供应商账互抵单
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    public int insertFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 修改供应商账互抵单
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    public int updateFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 变更供应商账互抵单
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    public int changeFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 批量删除供应商账互抵单
     *
     * @param vendorAccountBalanceBillSids 需要删除的供应商账互抵单ID
     * @return 结果
     */
    public int deleteFinVendorAccountBalanceBillByIds(List<Long>  vendorAccountBalanceBillSids);

    /**
     * 更改确认状态
     * @param finVendorAccountBalanceBill 请求
     * @return int
     */
    int check(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 删除明细表与流水复原
     * @author chenkw
     */
    void deleteItem(Long finVendorAccountBalanceBillSid, String handleStatus);

    /**
     * 查询流水明细
     * @author chenkw
     */
    List<FinVendorAccountBalanceBillItem> bookList(FinVendorAccountBalanceBillItem item);

    /**
     * 插入子表，附件表
     * @author chenkw
     */
    void insertChild(List<FinVendorAccountBalanceBillItem> itemList, List<FinVendorAccountBalanceBillAttachment> atmList, Long sid);

    /**
     * 生成流水，修改来源流水
     * @author chenkw
     */
    void insertBook(FinVendorAccountBalanceBill bill);

    /**
     * 作废供应商账互抵单
     *
     * @param accountBalanceBillSid 供应商账互抵单ID
     * @return 客户账互抵单
     */
    int invalid(Long accountBalanceBillSid);
}
