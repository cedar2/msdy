package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypePurchaseRequire;

/**
 * 单据类型_申购单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypePurchaseRequireService extends IService<ConDocTypePurchaseRequire>{
    /**
     * 查询单据类型_申购单
     * 
     * @param sid 单据类型_申购单ID
     * @return 单据类型_申购单
     */
    public ConDocTypePurchaseRequire selectConDocTypePurchaseRequireById(Long sid);

    /**
     * 查询单据类型_申购单列表
     * 
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 单据类型_申购单集合
     */
    public List<ConDocTypePurchaseRequire> selectConDocTypePurchaseRequireList(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 新增单据类型_申购单
     * 
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    public int insertConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 修改单据类型_申购单
     * 
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    public int updateConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 变更单据类型_申购单
     *
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    public int changeConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 批量删除单据类型_申购单
     * 
     * @param sids 需要删除的单据类型_申购单ID
     * @return 结果
     */
    public int deleteConDocTypePurchaseRequireByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypePurchaseRequire
    * @return
    */
    int changeStatus(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 更改确认状态
     * @param conDocTypePurchaseRequire
     * @return
     */
    int check(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

}
