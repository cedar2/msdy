package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBomType;

/**
 * BOM类型Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBomTypeService extends IService<ConBomType>{
    /**
     * 查询BOM类型
     * 
     * @param sid BOM类型ID
     * @return BOM类型
     */
    public ConBomType selectConBomTypeById(Long sid);

    /**
     * 查询BOM类型列表
     * 
     * @param conBomType BOM类型
     * @return BOM类型集合
     */
    public List<ConBomType> selectConBomTypeList(ConBomType conBomType);

    /**
     * 新增BOM类型
     * 
     * @param conBomType BOM类型
     * @return 结果
     */
    public int insertConBomType(ConBomType conBomType);

    /**
     * 修改BOM类型
     * 
     * @param conBomType BOM类型
     * @return 结果
     */
    public int updateConBomType(ConBomType conBomType);

    /**
     * 变更BOM类型
     *
     * @param conBomType BOM类型
     * @return 结果
     */
    public int changeConBomType(ConBomType conBomType);

    /**
     * 批量删除BOM类型
     * 
     * @param sids 需要删除的BOM类型ID
     * @return 结果
     */
    public int deleteConBomTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBomType
    * @return
    */
    int changeStatus(ConBomType conBomType);

    /**
     * 更改确认状态
     * @param conBomType
     * @return
     */
    int check(ConBomType conBomType);

}
