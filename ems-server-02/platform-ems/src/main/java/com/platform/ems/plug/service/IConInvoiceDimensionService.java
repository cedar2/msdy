package com.platform.ems.plug.service;


import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceDimension;

/**
 * 发票维度Service接口
 *
 * @author chenkw
 * @date 2021-08-11
 */
public interface IConInvoiceDimensionService extends IService<ConInvoiceDimension>{
    /**
     * 查询发票维度
     *
     * @param sid 发票维度ID
     * @return 发票维度
     */
    public ConInvoiceDimension selectConInvoiceDimensionById(Long sid);

    /**
     * 查询发票维度列表
     *
     * @param conInvoiceDimension 发票维度
     * @return 发票维度集合
     */
    public List<ConInvoiceDimension> selectConInvoiceDimensionList(ConInvoiceDimension conInvoiceDimension);

    /**
     * 新增发票维度
     *
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    public int insertConInvoiceDimension(ConInvoiceDimension conInvoiceDimension);

    /**
     * 修改发票维度
     *
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    public int updateConInvoiceDimension(ConInvoiceDimension conInvoiceDimension);

    /**
     * 变更发票维度
     *
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    public int changeConInvoiceDimension(ConInvoiceDimension conInvoiceDimension);

    /**
     * 批量删除发票维度
     *
     * @param sids 需要删除的发票维度ID
     * @return 结果
     */
    public int deleteConInvoiceDimensionByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conInvoiceDimension
     * @return
     */
    int changeStatus(ConInvoiceDimension conInvoiceDimension);

    /**
     * 更改确认状态
     * @param conInvoiceDimension
     * @return
     */
    int check(ConInvoiceDimension conInvoiceDimension);

    /**
     * 开票维度下拉框列表
     */
    List<ConInvoiceDimension> getInvoiceDimensionList();
}
