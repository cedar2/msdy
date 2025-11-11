package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConShelfType;

/**
 * 货架类型Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConShelfTypeService extends IService<ConShelfType>{
    /**
     * 查询货架类型
     * 
     * @param sid 货架类型ID
     * @return 货架类型
     */
    public ConShelfType selectConShelfTypeById(Long sid);

    /**
     * 查询货架类型列表
     * 
     * @param conShelfType 货架类型
     * @return 货架类型集合
     */
    public List<ConShelfType> selectConShelfTypeList(ConShelfType conShelfType);

    /**
     * 新增货架类型
     * 
     * @param conShelfType 货架类型
     * @return 结果
     */
    public int insertConShelfType(ConShelfType conShelfType);

    /**
     * 修改货架类型
     * 
     * @param conShelfType 货架类型
     * @return 结果
     */
    public int updateConShelfType(ConShelfType conShelfType);

    /**
     * 变更货架类型
     *
     * @param conShelfType 货架类型
     * @return 结果
     */
    public int changeConShelfType(ConShelfType conShelfType);

    /**
     * 批量删除货架类型
     * 
     * @param sids 需要删除的货架类型ID
     * @return 结果
     */
    public int deleteConShelfTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conShelfType
    * @return
    */
    int changeStatus(ConShelfType conShelfType);

    /**
     * 更改确认状态
     * @param conShelfType
     * @return
     */
    int check(ConShelfType conShelfType);

}
