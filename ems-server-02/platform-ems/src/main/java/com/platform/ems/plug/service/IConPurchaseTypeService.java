package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPurchaseType;

/**
 * 采购类型Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPurchaseTypeService extends IService<ConPurchaseType>{
    /**
     * 查询采购类型
     *
     * @param sid 采购类型ID
     * @return 采购类型
     */
    public ConPurchaseType selectConPurchaseTypeById(Long sid);

    /**
     * 查询采购类型列表
     *
     * @param conPurchaseType 采购类型
     * @return 采购类型集合
     */
    public List<ConPurchaseType> selectConPurchaseTypeList(ConPurchaseType conPurchaseType);

    /**
     * 新增采购类型
     *
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    public int insertConPurchaseType(ConPurchaseType conPurchaseType);

    /**
     * 修改采购类型
     *
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    public int updateConPurchaseType(ConPurchaseType conPurchaseType);

    /**
     * 变更采购类型
     *
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    public int changeConPurchaseType(ConPurchaseType conPurchaseType);

    /**
     * 批量删除采购类型
     *
     * @param sids 需要删除的采购类型ID
     * @return 结果
     */
    public int deleteConPurchaseTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPurchaseType
    * @return
    */
    int changeStatus(ConPurchaseType conPurchaseType);

    /**
     * 更改确认状态
     * @param conPurchaseType
     * @return
     */
    int check(ConPurchaseType conPurchaseType);

    /**  获取下拉列表 */
    List<ConPurchaseType> getConPurchaseTypeList();

    List<ConPurchaseType> getList(ConPurchaseType conPurchaseType);
}
