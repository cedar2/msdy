package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConLocationType;

/**
 * 库位类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConLocationTypeService extends IService<ConLocationType>{
    /**
     * 查询库位类型
     * 
     * @param sid 库位类型ID
     * @return 库位类型
     */
    public ConLocationType selectConLocationTypeById(Long sid);

    /**
     * 查询库位类型列表
     * 
     * @param conLocationType 库位类型
     * @return 库位类型集合
     */
    public List<ConLocationType> selectConLocationTypeList(ConLocationType conLocationType);

    /**
     * 新增库位类型
     * 
     * @param conLocationType 库位类型
     * @return 结果
     */
    public int insertConLocationType(ConLocationType conLocationType);

    /**
     * 修改库位类型
     * 
     * @param conLocationType 库位类型
     * @return 结果
     */
    public int updateConLocationType(ConLocationType conLocationType);

    /**
     * 变更库位类型
     *
     * @param conLocationType 库位类型
     * @return 结果
     */
    public int changeConLocationType(ConLocationType conLocationType);

    /**
     * 批量删除库位类型
     * 
     * @param sids 需要删除的库位类型ID
     * @return 结果
     */
    public int deleteConLocationTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conLocationType
    * @return
    */
    int changeStatus(ConLocationType conLocationType);

    /**
     * 更改确认状态
     * @param conLocationType
     * @return
     */
    int check(ConLocationType conLocationType);

}
