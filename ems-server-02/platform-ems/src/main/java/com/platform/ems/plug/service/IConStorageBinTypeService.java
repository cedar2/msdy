package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStorageBinType;

/**
 * 仓位存储类型Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConStorageBinTypeService extends IService<ConStorageBinType>{
    /**
     * 查询仓位存储类型
     * 
     * @param sid 仓位存储类型ID
     * @return 仓位存储类型
     */
    public ConStorageBinType selectConStorageBinTypeById(Long sid);

    /**
     * 查询仓位存储类型列表
     * 
     * @param conStorageBinType 仓位存储类型
     * @return 仓位存储类型集合
     */
    public List<ConStorageBinType> selectConStorageBinTypeList(ConStorageBinType conStorageBinType);

    /**
     * 新增仓位存储类型
     * 
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    public int insertConStorageBinType(ConStorageBinType conStorageBinType);

    /**
     * 修改仓位存储类型
     * 
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    public int updateConStorageBinType(ConStorageBinType conStorageBinType);

    /**
     * 变更仓位存储类型
     *
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    public int changeConStorageBinType(ConStorageBinType conStorageBinType);

    /**
     * 批量删除仓位存储类型
     * 
     * @param sids 需要删除的仓位存储类型ID
     * @return 结果
     */
    public int deleteConStorageBinTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conStorageBinType
    * @return
    */
    int changeStatus(ConStorageBinType conStorageBinType);

    /**
     * 更改确认状态
     * @param conStorageBinType
     * @return
     */
    int check(ConStorageBinType conStorageBinType);

}
