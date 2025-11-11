package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInvoiceType;

/**
 * 发票类型Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConInvoiceTypeService extends IService<ConInvoiceType>{
    /**
     * 查询发票类型
     *
     * @param sid 发票类型ID
     * @return 发票类型
     */
    public ConInvoiceType selectConInvoiceTypeById(Long sid);

    /**
     * 查询发票类型列表
     *
     * @param conInvoiceType 发票类型
     * @return 发票类型集合
     */
    public List<ConInvoiceType> selectConInvoiceTypeList(ConInvoiceType conInvoiceType);

    /**
     * 新增发票类型
     *
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    public int insertConInvoiceType(ConInvoiceType conInvoiceType);

    /**
     * 修改发票类型
     *
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    public int updateConInvoiceType(ConInvoiceType conInvoiceType);

    /**
     * 变更发票类型
     *
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    public int changeConInvoiceType(ConInvoiceType conInvoiceType);

    /**
     * 批量删除发票类型
     *
     * @param sids 需要删除的发票类型ID
     * @return 结果
     */
    public int deleteConInvoiceTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conInvoiceType
    * @return
    */
    int changeStatus(ConInvoiceType conInvoiceType);

    /**
     * 更改确认状态
     * @param conInvoiceType
     * @return
     */
    int check(ConInvoiceType conInvoiceType);

    /**  获取下拉列表 */
    List<ConInvoiceType> getConInvoiceTypeList(ConInvoiceType conInvoiceType);
}
