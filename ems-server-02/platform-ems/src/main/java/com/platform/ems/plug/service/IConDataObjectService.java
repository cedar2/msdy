package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDataObject;

/**
 * 数据对象Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConDataObjectService extends IService<ConDataObject>{
    /**
     * 查询数据对象
     * 
     * @param sid 数据对象ID
     * @return 数据对象
     */
    public ConDataObject selectConDataObjectById(Long sid);

    /**
     * 查询数据对象列表
     * 
     * @param conDataObject 数据对象
     * @return 数据对象集合
     */
    public List<ConDataObject> selectConDataObjectList(ConDataObject conDataObject);

    /**
     * 新增数据对象
     * 
     * @param conDataObject 数据对象
     * @return 结果
     */
    public int insertConDataObject(ConDataObject conDataObject);

    /**
     * 修改数据对象
     * 
     * @param conDataObject 数据对象
     * @return 结果
     */
    public int updateConDataObject(ConDataObject conDataObject);

    /**
     * 变更数据对象
     *
     * @param conDataObject 数据对象
     * @return 结果
     */
    public int changeConDataObject(ConDataObject conDataObject);

    /**
     * 批量删除数据对象
     * 
     * @param sids 需要删除的数据对象ID
     * @return 结果
     */
    public int deleteConDataObjectByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conDataObject
    * @return
    */
    int changeStatus(ConDataObject conDataObject);

    /**
     * 更改确认状态
     * @param conDataObject
     * @return
     */
    int check(ConDataObject conDataObject);

}
