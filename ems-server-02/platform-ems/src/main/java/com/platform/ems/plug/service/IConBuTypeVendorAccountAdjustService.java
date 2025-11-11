package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeVendorAccountAdjust;

/**
 * 业务类型_供应商调账单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeVendorAccountAdjustService extends IService<ConBuTypeVendorAccountAdjust>{
    /**
     * 查询业务类型_供应商调账单
     * 
     * @param sid 业务类型_供应商调账单ID
     * @return 业务类型_供应商调账单
     */
    public ConBuTypeVendorAccountAdjust selectConBuTypeVendorAccountAdjustById(Long sid);

    /**
     * 查询业务类型_供应商调账单列表
     * 
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 业务类型_供应商调账单集合
     */
    public List<ConBuTypeVendorAccountAdjust> selectConBuTypeVendorAccountAdjustList(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 新增业务类型_供应商调账单
     * 
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    public int insertConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 修改业务类型_供应商调账单
     * 
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    public int updateConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 变更业务类型_供应商调账单
     *
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    public int changeConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 批量删除业务类型_供应商调账单
     * 
     * @param sids 需要删除的业务类型_供应商调账单ID
     * @return 结果
     */
    public int deleteConBuTypeVendorAccountAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeVendorAccountAdjust
    * @return
    */
    int changeStatus(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 更改确认状态
     * @param conBuTypeVendorAccountAdjust
     * @return
     */
    int check(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

}
