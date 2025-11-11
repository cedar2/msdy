package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMaterialType;

/**
 * 物料类型Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConMaterialTypeService extends IService<ConMaterialType>{
    /**
     * 查询物料类型
     *
     * @param sid 物料类型ID
     * @return 物料类型
     */
    public ConMaterialType selectConMaterialTypeById(Long sid);

    /**
     * 查询物料类型列表
     *
     * @param conMaterialType 物料类型
     * @return 物料类型集合
     */
    public List<ConMaterialType> selectConMaterialTypeList(ConMaterialType conMaterialType);

    /**
     * 新增物料类型
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    public int insertConMaterialType(ConMaterialType conMaterialType);

    /**
     * 修改物料类型
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    public int updateConMaterialType(ConMaterialType conMaterialType);

    /**
     * 变更物料类型
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    public int changeConMaterialType(ConMaterialType conMaterialType);

    /**
     * 批量删除物料类型
     *
     * @param sids 需要删除的物料类型ID
     * @return 结果
     */
    public int deleteConMaterialTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conMaterialType
    * @return
    */
    int changeStatus(ConMaterialType conMaterialType);

    /**
     * 更改确认状态
     * @param conMaterialType
     * @return
     */
    int check(ConMaterialType conMaterialType);

    /**  获取下拉列表 */
    List<ConMaterialType> getConMaterialTypeList();

    /**  获取下拉列表 */
    List<ConMaterialType> getList(ConMaterialType conMaterialType);
}
