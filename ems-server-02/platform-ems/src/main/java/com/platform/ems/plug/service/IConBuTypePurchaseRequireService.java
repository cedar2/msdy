package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;

/**
 * 业务类型_申购单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypePurchaseRequireService extends IService<ConBuTypePurchaseRequire>{
    /**
     * 查询业务类型_申购单
     * 
     * @param sid 业务类型_申购单ID
     * @return 业务类型_申购单
     */
    public ConBuTypePurchaseRequire selectConBuTypePurchaseRequireById(Long sid);

    /**
     * 查询业务类型_申购单列表
     * 
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 业务类型_申购单集合
     */
    public List<ConBuTypePurchaseRequire> selectConBuTypePurchaseRequireList(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 新增业务类型_申购单
     * 
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    public int insertConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 修改业务类型_申购单
     * 
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    public int updateConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 变更业务类型_申购单
     *
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    public int changeConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 批量删除业务类型_申购单
     * 
     * @param sids 需要删除的业务类型_申购单ID
     * @return 结果
     */
    public int deleteConBuTypePurchaseRequireByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypePurchaseRequire
    * @return
    */
    int changeStatus(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 更改确认状态
     * @param conBuTypePurchaseRequire
     * @return
     */
    int check(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

}
