package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceDimensionSale;

/**
 * 发票维度_销售Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConInvoiceDimensionSaleService extends IService<ConInvoiceDimensionSale>{
    /**
     * 查询发票维度_销售
     * 
     * @param sid 发票维度_销售ID
     * @return 发票维度_销售
     */
    public ConInvoiceDimensionSale selectConInvoiceDimensionSaleById(Long sid);

    /**
     * 查询发票维度_销售列表
     * 
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 发票维度_销售集合
     */
    public List<ConInvoiceDimensionSale> selectConInvoiceDimensionSaleList(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 新增发票维度_销售
     * 
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    public int insertConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 修改发票维度_销售
     * 
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    public int updateConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 变更发票维度_销售
     *
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    public int changeConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 批量删除发票维度_销售
     * 
     * @param sids 需要删除的发票维度_销售ID
     * @return 结果
     */
    public int deleteConInvoiceDimensionSaleByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conInvoiceDimensionSale
    * @return
    */
    int changeStatus(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 更改确认状态
     * @param conInvoiceDimensionSale
     * @return
     */
    int check(ConInvoiceDimensionSale conInvoiceDimensionSale);

}
