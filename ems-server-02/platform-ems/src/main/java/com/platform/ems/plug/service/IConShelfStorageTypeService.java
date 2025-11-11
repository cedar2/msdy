package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConShelfStorageType;

/**
 * 货架存储类型Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConShelfStorageTypeService extends IService<ConShelfStorageType>{
    /**
     * 查询货架存储类型
     * 
     * @param sid 货架存储类型ID
     * @return 货架存储类型
     */
    public ConShelfStorageType selectConShelfStorageTypeById(Long sid);

    /**
     * 查询货架存储类型列表
     * 
     * @param conShelfStorageType 货架存储类型
     * @return 货架存储类型集合
     */
    public List<ConShelfStorageType> selectConShelfStorageTypeList(ConShelfStorageType conShelfStorageType);

    /**
     * 新增货架存储类型
     * 
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    public int insertConShelfStorageType(ConShelfStorageType conShelfStorageType);

    /**
     * 修改货架存储类型
     * 
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    public int updateConShelfStorageType(ConShelfStorageType conShelfStorageType);

    /**
     * 变更货架存储类型
     *
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    public int changeConShelfStorageType(ConShelfStorageType conShelfStorageType);

    /**
     * 批量删除货架存储类型
     * 
     * @param sids 需要删除的货架存储类型ID
     * @return 结果
     */
    public int deleteConShelfStorageTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conShelfStorageType
    * @return
    */
    int changeStatus(ConShelfStorageType conShelfStorageType);

    /**
     * 更改确认状态
     * @param conShelfStorageType
     * @return
     */
    int check(ConShelfStorageType conShelfStorageType);

}
