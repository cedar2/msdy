package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.plug.domain.ConDocTypeMaterialRequisition;

/**
 * 单据类型_领退料单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeMaterialRequisitionService extends IService<ConDocTypeMaterialRequisition>{
    /**
     * 查询单据类型_领退料单
     *
     * @param sid 单据类型_领退料单ID
     * @return 单据类型_领退料单
     */
    public ConDocTypeMaterialRequisition selectConDocTypeMaterialRequisitionById(Long sid);

    public List<ConDocTypeMaterialRequisition> getList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);
    /**
     * 查询单据类型_领退料单列表
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 单据类型_领退料单集合
     */
    public List<ConDocTypeMaterialRequisition> selectConDocTypeMaterialRequisitionList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 新增单据类型_领退料单
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    public int insertConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 修改单据类型_领退料单
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    public int updateConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 变更单据类型_领退料单
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    public int changeConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 批量删除单据类型_领退料单
     *
     * @param sids 需要删除的单据类型_领退料单ID
     * @return 结果
     */
    public int deleteConDocTypeMaterialRequisitionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeMaterialRequisition
    * @return
    */
    int changeStatus(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 更改确认状态
     * @param conDocTypeMaterialRequisition
     * @return
     */
    int check(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

}
