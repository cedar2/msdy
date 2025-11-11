package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceDimensionPurcahse;

/**
 * 发票维度_采购Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConInvoiceDimensionPurcahseService extends IService<ConInvoiceDimensionPurcahse>{
    /**
     * 查询发票维度_采购
     * 
     * @param sid 发票维度_采购ID
     * @return 发票维度_采购
     */
    public ConInvoiceDimensionPurcahse selectConInvoiceDimensionPurcahseById(Long sid);

    /**
     * 查询发票维度_采购列表
     * 
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 发票维度_采购集合
     */
    public List<ConInvoiceDimensionPurcahse> selectConInvoiceDimensionPurcahseList(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 新增发票维度_采购
     * 
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    public int insertConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 修改发票维度_采购
     * 
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    public int updateConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 变更发票维度_采购
     *
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    public int changeConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 批量删除发票维度_采购
     * 
     * @param sids 需要删除的发票维度_采购ID
     * @return 结果
     */
    public int deleteConInvoiceDimensionPurcahseByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conInvoiceDimensionPurcahse
    * @return
    */
    int changeStatus(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 更改确认状态
     * @param conInvoiceDimensionPurcahse
     * @return
     */
    int check(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

}
