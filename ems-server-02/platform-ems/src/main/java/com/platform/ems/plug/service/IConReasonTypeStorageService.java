package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocCategoryInventoryDocument;
import com.platform.ems.plug.domain.ConReasonTypeStorage;

/**
 * 原因类型(库存管理)Service接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConReasonTypeStorageService extends IService<ConReasonTypeStorage>{
    /**
     * 查询原因类型(库存管理)
     * 
     * @param sid 原因类型(库存管理)ID
     * @return 原因类型(库存管理)
     */
    public ConReasonTypeStorage selectConReasonTypeStorageById(Long sid);
    public List<ConReasonTypeStorage> getList();

    /**
     * 查询原因类型(库存管理)列表
     * 
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 原因类型(库存管理)集合
     */
    public List<ConReasonTypeStorage> selectConReasonTypeStorageList(ConReasonTypeStorage conReasonTypeStorage);

    /**
     * 新增原因类型(库存管理)
     * 
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    public int insertConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage);

    /**
     * 修改原因类型(库存管理)
     * 
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    public int updateConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage);

    /**
     * 变更原因类型(库存管理)
     *
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    public int changeConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage);

    /**
     * 批量删除原因类型(库存管理)
     * 
     * @param sids 需要删除的原因类型(库存管理)ID
     * @return 结果
     */
    public int deleteConReasonTypeStorageByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conReasonTypeStorage
    * @return
    */
    int changeStatus(ConReasonTypeStorage conReasonTypeStorage);

    /**
     * 更改确认状态
     * @param conReasonTypeStorage
     * @return
     */
    int check(ConReasonTypeStorage conReasonTypeStorage);

}
