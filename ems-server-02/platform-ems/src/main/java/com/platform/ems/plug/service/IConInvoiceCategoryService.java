package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.domain.ConInvoiceCategory;

/**
 * 发票类别Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConInvoiceCategoryService extends IService<ConInvoiceCategory>{
    /**
     * 查询发票类别
     *
     * @param sid 发票类别ID
     * @return 发票类别
     */
    public ConInvoiceCategory selectConInvoiceCategoryById(Long sid);

    /**
     * 查询发票类别列表
     *
     * @param conInvoiceCategory 发票类别
     * @return 发票类别集合
     */
    public List<ConInvoiceCategory> selectConInvoiceCategoryList(ConInvoiceCategory conInvoiceCategory);

    /**
     * 新增发票类别
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    public int insertConInvoiceCategory(ConInvoiceCategory conInvoiceCategory);

    /**
     * 修改发票类别
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    public int updateConInvoiceCategory(ConInvoiceCategory conInvoiceCategory);

    /**
     * 变更发票类别
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    public int changeConInvoiceCategory(ConInvoiceCategory conInvoiceCategory);

    /**
     * 批量删除发票类别
     *
     * @param sids 需要删除的发票类别ID
     * @return 结果
     */
    public int deleteConInvoiceCategoryByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conInvoiceCategory
    * @return
    */
    int changeStatus(ConInvoiceCategory conInvoiceCategory);

    /**
     * 更改确认状态
     * @param conInvoiceCategory
     * @return
     */
    int check(ConInvoiceCategory conInvoiceCategory);

    /**  获取下拉列表 */
    List<ConInvoiceCategory> getConInvoiceCategoryList();
}
