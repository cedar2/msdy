package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryP;

/**
 * 类别_采购发票行项目Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConInvoiceItemCategoryPService extends IService<ConInvoiceItemCategoryP>{
    /**
     * 查询类别_采购发票行项目
     * 
     * @param sid 类别_采购发票行项目ID
     * @return 类别_采购发票行项目
     */
    public ConInvoiceItemCategoryP selectConInvoiceItemCategoryPById(Long sid);

    /**
     * 查询类别_采购发票行项目列表
     * 
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 类别_采购发票行项目集合
     */
    public List<ConInvoiceItemCategoryP> selectConInvoiceItemCategoryPList(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 新增类别_采购发票行项目
     * 
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    public int insertConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 修改类别_采购发票行项目
     * 
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    public int updateConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 变更类别_采购发票行项目
     *
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    public int changeConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 批量删除类别_采购发票行项目
     * 
     * @param sids 需要删除的类别_采购发票行项目ID
     * @return 结果
     */
    public int deleteConInvoiceItemCategoryPByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conInvoiceItemCategoryP
    * @return
    */
    int changeStatus(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 更改确认状态
     * @param conInvoiceItemCategoryP
     * @return
     */
    int check(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

}
