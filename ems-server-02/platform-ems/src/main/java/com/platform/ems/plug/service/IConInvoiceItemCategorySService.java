package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryS;

/**
 * 类别_销售发票行项目Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConInvoiceItemCategorySService extends IService<ConInvoiceItemCategoryS>{
    /**
     * 查询类别_销售发票行项目
     * 
     * @param sid 类别_销售发票行项目ID
     * @return 类别_销售发票行项目
     */
    public ConInvoiceItemCategoryS selectConInvoiceItemCategorySById(Long sid);

    /**
     * 查询类别_销售发票行项目列表
     * 
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 类别_销售发票行项目集合
     */
    public List<ConInvoiceItemCategoryS> selectConInvoiceItemCategorySList(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 新增类别_销售发票行项目
     * 
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    public int insertConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 修改类别_销售发票行项目
     * 
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    public int updateConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 变更类别_销售发票行项目
     *
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    public int changeConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 批量删除类别_销售发票行项目
     * 
     * @param sids 需要删除的类别_销售发票行项目ID
     * @return 结果
     */
    public int deleteConInvoiceItemCategorySByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conInvoiceItemCategoryS
    * @return
    */
    int changeStatus(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 更改确认状态
     * @param conInvoiceItemCategoryS
     * @return
     */
    int check(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

}
