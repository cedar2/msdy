package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCustomerTagItem;

import java.util.List;

/**
 * 客户标签(分组)明细Service接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface IBasCustomerTagItemService extends IService<BasCustomerTagItem> {
    /**
     * 查询客户标签(分组)明细
     *
     * @param customerTagItemSid 客户标签(分组)明细ID
     * @return 客户标签(分组)明细
     */
    public BasCustomerTagItem selectBasCustomerTagItemById(Long customerTagItemSid);

    /**
     * 查询客户标签(分组)明细列表
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 客户标签(分组)明细集合
     */
    public List<BasCustomerTagItem> selectBasCustomerTagItemList(BasCustomerTagItem basCustomerTagItem);

    /**
     * 新增客户标签(分组)明细
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    public int insertBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem);

    /**
     * 修改客户标签(分组)明细
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    public int updateBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem);

    /**
     * 变更客户标签(分组)明细
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    public int changeBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem);

    /**
     * 批量删除客户标签(分组)明细
     *
     * @param customerTagItemSids 需要删除的客户标签(分组)明细ID
     * @return 结果
     */
    public int deleteBasCustomerTagItemByIds(List<Long> customerTagItemSids);

}
