package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConReserveType;

/**
 * 预留类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConReserveTypeService extends IService<ConReserveType>{
    /**
     * 查询预留类型
     * 
     * @param sid 预留类型ID
     * @return 预留类型
     */
    public ConReserveType selectConReserveTypeById(Long sid);

    /**
     * 查询预留类型列表
     * 
     * @param conReserveType 预留类型
     * @return 预留类型集合
     */
    public List<ConReserveType> selectConReserveTypeList(ConReserveType conReserveType);

    /**
     * 新增预留类型
     * 
     * @param conReserveType 预留类型
     * @return 结果
     */
    public int insertConReserveType(ConReserveType conReserveType);

    /**
     * 修改预留类型
     * 
     * @param conReserveType 预留类型
     * @return 结果
     */
    public int updateConReserveType(ConReserveType conReserveType);

    /**
     * 变更预留类型
     *
     * @param conReserveType 预留类型
     * @return 结果
     */
    public int changeConReserveType(ConReserveType conReserveType);

    /**
     * 批量删除预留类型
     * 
     * @param sids 需要删除的预留类型ID
     * @return 结果
     */
    public int deleteConReserveTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conReserveType
    * @return
    */
    int changeStatus(ConReserveType conReserveType);

    /**
     * 更改确认状态
     * @param conReserveType
     * @return
     */
    int check(ConReserveType conReserveType);

}
