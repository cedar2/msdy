package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorTagItem;

import java.util.List;

/**
 * 供应商标签(分组)明细Service接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface IBasVendorTagItemService extends IService<BasVendorTagItem> {
    /**
     * 查询供应商标签(分组)明细
     *
     * @param vendorTagItemSid 供应商标签(分组)明细ID
     * @return 供应商标签(分组)明细
     */
    public BasVendorTagItem selectBasVendorTagItemById(Long vendorTagItemSid);

    /**
     * 查询供应商标签(分组)明细列表
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 供应商标签(分组)明细集合
     */
    public List<BasVendorTagItem> selectBasVendorTagItemList(BasVendorTagItem basVendorTagItem);

    /**
     * 新增供应商标签(分组)明细
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    public int insertBasVendorTagItem(BasVendorTagItem basVendorTagItem);

    /**
     * 修改供应商标签(分组)明细
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    public int updateBasVendorTagItem(BasVendorTagItem basVendorTagItem);

    /**
     * 变更供应商标签(分组)明细
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    public int changeBasVendorTagItem(BasVendorTagItem basVendorTagItem);

    /**
     * 批量删除供应商标签(分组)明细
     *
     * @param vendorTagItemSids 需要删除的供应商标签(分组)明细ID
     * @return 结果
     */
    public int deleteBasVendorTagItemByIds(List<Long> vendorTagItemSids);

}
