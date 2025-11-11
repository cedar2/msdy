package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeMaterialRequisition;

/**
 * 业务类型_领退料单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeMaterialRequisitionService extends IService<ConBuTypeMaterialRequisition>{
    /**
     * 查询业务类型_领退料单
     * 
     * @param sid 业务类型_领退料单ID
     * @return 业务类型_领退料单
     */
    public ConBuTypeMaterialRequisition selectConBuTypeMaterialRequisitionById(Long sid);
    public List<ConBuTypeMaterialRequisition> getList();
    /**
     * 查询业务类型_领退料单列表
     * 
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 业务类型_领退料单集合
     */
    public List<ConBuTypeMaterialRequisition> selectConBuTypeMaterialRequisitionList(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

    /**
     * 新增业务类型_领退料单
     * 
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    public int insertConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

    /**
     * 修改业务类型_领退料单
     * 
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    public int updateConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

    /**
     * 变更业务类型_领退料单
     *
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    public int changeConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

    /**
     * 批量删除业务类型_领退料单
     * 
     * @param sids 需要删除的业务类型_领退料单ID
     * @return 结果
     */
    public int deleteConBuTypeMaterialRequisitionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeMaterialRequisition
    * @return
    */
    int changeStatus(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

    /**
     * 更改确认状态
     * @param conBuTypeMaterialRequisition
     * @return
     */
    int check(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);

}
