package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStorageUnitType;

/**
 * 托盘类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConStorageUnitTypeService extends IService<ConStorageUnitType>{
    /**
     * 查询托盘类型
     * 
     * @param sid 托盘类型ID
     * @return 托盘类型
     */
    public ConStorageUnitType selectConStorageUnitTypeById(Long sid);

    /**
     * 查询托盘类型列表
     * 
     * @param conStorageUnitType 托盘类型
     * @return 托盘类型集合
     */
    public List<ConStorageUnitType> selectConStorageUnitTypeList(ConStorageUnitType conStorageUnitType);

    /**
     * 新增托盘类型
     * 
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    public int insertConStorageUnitType(ConStorageUnitType conStorageUnitType);

    /**
     * 修改托盘类型
     * 
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    public int updateConStorageUnitType(ConStorageUnitType conStorageUnitType);

    /**
     * 变更托盘类型
     *
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    public int changeConStorageUnitType(ConStorageUnitType conStorageUnitType);

    /**
     * 批量删除托盘类型
     * 
     * @param sids 需要删除的托盘类型ID
     * @return 结果
     */
    public int deleteConStorageUnitTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conStorageUnitType
    * @return
    */
    int changeStatus(ConStorageUnitType conStorageUnitType);

    /**
     * 更改确认状态
     * @param conStorageUnitType
     * @return
     */
    int check(ConStorageUnitType conStorageUnitType);

}
