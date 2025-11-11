package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.*;

/**
 * 客户账互抵单Service接口
 *
 * @author qhq
 * @date 2021-05-27
 */
public interface IFinCustomerAccountBalanceBillService extends IService<FinCustomerAccountBalanceBill>{
    /**
     * 查询客户账互抵单
     *
     * @param customerAccountBalanceBillSid 客户账互抵单ID
     * @return 客户账互抵单
     */
    public FinCustomerAccountBalanceBill selectFinCustomerAccountBalanceBillById(Long customerAccountBalanceBillSid);

    /**
     * 查询客户账互抵单列表
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 客户账互抵单集合
     */
    public List<FinCustomerAccountBalanceBill> selectFinCustomerAccountBalanceBillList(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 新增客户账互抵单
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    public int insertFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 修改客户账互抵单
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    public int updateFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 变更客户账互抵单
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    public int changeFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 批量删除客户账互抵单
     *
     * @param customerAccountBalanceBillSids 需要删除的客户账互抵单ID
     * @return 结果
     */
    public int deleteFinCustomerAccountBalanceBillByIds(List<Long>  customerAccountBalanceBillSids);

    /**
     * 更改确认状态
     * @param finCustomerAccountBalanceBill 请求
     */
    int check(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill);

    /**
     * 删除明细表与流水复原
     * @author chenkw
     */
    void deleteItem(Long finCustomerAccountBalanceBillSid, String handleStatus);

    /**
     * 查询流水明细
     * @author chenkw
     */
    List<FinCustomerAccountBalanceBillItem> bookList(FinCustomerAccountBalanceBillItem item);

    /**
     * 插入子表，附件表
     * @author chenkw
     */
    void insertChild(List<FinCustomerAccountBalanceBillItem> itemList, List<FinCustomerAccountBalanceBillAttachment> atmList, Long sid);

    /**
     * 生成流水，修改来源流水
     * @author chenkw
     */
    void insertBook(FinCustomerAccountBalanceBill bill);

    /**
     * 作废客户账互抵单
     *
     * @param accountBalanceBillSid 客户账互抵单ID
     * @return 客户账互抵单
     */
    int invalid(Long accountBalanceBillSid);

}
