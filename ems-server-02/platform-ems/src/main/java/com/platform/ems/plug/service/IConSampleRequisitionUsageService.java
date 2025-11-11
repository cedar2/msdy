package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConSampleRequisitionUsage;

/**
 * 样品出库用途Service接口
 * 
 * @author yangqz
 * @date 2022-04-24
 */
public interface IConSampleRequisitionUsageService extends IService<ConSampleRequisitionUsage>{
    /**
     * 查询样品出库用途
     * 
     * @param sid 样品出库用途ID
     * @return 样品出库用途
     */
    public ConSampleRequisitionUsage selectConSampleRequisitionUsageById(Long sid);

    /**
     * 查询样品出库用途列表
     * 
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 样品出库用途集合
     */
    public List<ConSampleRequisitionUsage> selectConSampleRequisitionUsageList(ConSampleRequisitionUsage conSampleRequisitionUsage);
    /**
     * 查询样品出库用途下拉列表
     */
    public List<ConSampleRequisitionUsage> getList(ConSampleRequisitionUsage conSampleRequisitionUsage);
    /**
     * 新增样品出库用途
     * 
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    public int insertConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage);

    /**
     * 修改样品出库用途
     * 
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    public int updateConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage);

    /**
     * 变更样品出库用途
     *
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    public int changeConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage);

    /**
     * 批量删除样品出库用途
     * 
     * @param sids 需要删除的样品出库用途ID
     * @return 结果
     */
    public int deleteConSampleRequisitionUsageByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSampleRequisitionUsage
    * @return
    */
    int changeStatus(ConSampleRequisitionUsage conSampleRequisitionUsage);

    /**
     * 更改确认状态
     * @param conSampleRequisitionUsage
     * @return
     */
    int check(ConSampleRequisitionUsage conSampleRequisitionUsage);

}
