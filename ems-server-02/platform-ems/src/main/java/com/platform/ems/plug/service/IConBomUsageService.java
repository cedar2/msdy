package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBomUsage;

/**
 * BOM用途Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBomUsageService extends IService<ConBomUsage>{
    /**
     * 查询BOM用途
     * 
     * @param sid BOM用途ID
     * @return BOM用途
     */
    public ConBomUsage selectConBomUsageById(Long sid);

    /**
     * 查询BOM用途列表
     * 
     * @param conBomUsage BOM用途
     * @return BOM用途集合
     */
    public List<ConBomUsage> selectConBomUsageList(ConBomUsage conBomUsage);

    /**
     * 新增BOM用途
     * 
     * @param conBomUsage BOM用途
     * @return 结果
     */
    public int insertConBomUsage(ConBomUsage conBomUsage);

    /**
     * 修改BOM用途
     * 
     * @param conBomUsage BOM用途
     * @return 结果
     */
    public int updateConBomUsage(ConBomUsage conBomUsage);

    /**
     * 变更BOM用途
     *
     * @param conBomUsage BOM用途
     * @return 结果
     */
    public int changeConBomUsage(ConBomUsage conBomUsage);

    /**
     * 批量删除BOM用途
     * 
     * @param sids 需要删除的BOM用途ID
     * @return 结果
     */
    public int deleteConBomUsageByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBomUsage
    * @return
    */
    int changeStatus(ConBomUsage conBomUsage);

    /**
     * 更改确认状态
     * @param conBomUsage
     * @return
     */
    int check(ConBomUsage conBomUsage);

}
