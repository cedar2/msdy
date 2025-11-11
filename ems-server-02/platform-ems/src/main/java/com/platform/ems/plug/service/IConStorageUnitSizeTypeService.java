package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStorageUnitSizeType;

/**
 * 托盘规格类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConStorageUnitSizeTypeService extends IService<ConStorageUnitSizeType>{
    /**
     * 查询托盘规格类型
     * 
     * @param sid 托盘规格类型ID
     * @return 托盘规格类型
     */
    public ConStorageUnitSizeType selectConStorageUnitSizeTypeById(Long sid);

    /**
     * 查询托盘规格类型列表
     * 
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 托盘规格类型集合
     */
    public List<ConStorageUnitSizeType> selectConStorageUnitSizeTypeList(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 新增托盘规格类型
     * 
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    public int insertConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 修改托盘规格类型
     * 
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    public int updateConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 变更托盘规格类型
     *
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    public int changeConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 批量删除托盘规格类型
     * 
     * @param sids 需要删除的托盘规格类型ID
     * @return 结果
     */
    public int deleteConStorageUnitSizeTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conStorageUnitSizeType
    * @return
    */
    int changeStatus(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 更改确认状态
     * @param conStorageUnitSizeType
     * @return
     */
    int check(ConStorageUnitSizeType conStorageUnitSizeType);

}
