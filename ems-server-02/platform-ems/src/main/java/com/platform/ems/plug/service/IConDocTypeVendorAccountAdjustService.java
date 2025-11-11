package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeVendorAccountAdjust;

/**
 * 单据类型_供应商调账单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeVendorAccountAdjustService extends IService<ConDocTypeVendorAccountAdjust>{
    /**
     * 查询单据类型_供应商调账单
     * 
     * @param sid 单据类型_供应商调账单ID
     * @return 单据类型_供应商调账单
     */
    public ConDocTypeVendorAccountAdjust selectConDocTypeVendorAccountAdjustById(Long sid);

    /**
     * 查询单据类型_供应商调账单列表
     * 
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 单据类型_供应商调账单集合
     */
    public List<ConDocTypeVendorAccountAdjust> selectConDocTypeVendorAccountAdjustList(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 新增单据类型_供应商调账单
     * 
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    public int insertConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 修改单据类型_供应商调账单
     * 
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    public int updateConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 变更单据类型_供应商调账单
     *
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    public int changeConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 批量删除单据类型_供应商调账单
     * 
     * @param sids 需要删除的单据类型_供应商调账单ID
     * @return 结果
     */
    public int deleteConDocTypeVendorAccountAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeVendorAccountAdjust
    * @return
    */
    int changeStatus(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 更改确认状态
     * @param conDocTypeVendorAccountAdjust
     * @return
     */
    int check(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

}
