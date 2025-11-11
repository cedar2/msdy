package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConProductTechniqueType;

/**
 * 生产工艺方法(编织方法)Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConProductTechniqueTypeService extends IService<ConProductTechniqueType>{
    /**
     * 查询生产工艺方法(编织方法)
     *
     * @param sid 生产工艺方法(编织方法)ID
     * @return 生产工艺方法(编织方法)
     */
    public ConProductTechniqueType selectConProductTechniqueTypeById(Long sid);

    /**
     * 查询生产工艺方法(编织方法)列表
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 生产工艺方法(编织方法)集合
     */
    public List<ConProductTechniqueType> selectConProductTechniqueTypeList(ConProductTechniqueType conProductTechniqueType);

    /**
     * 新增生产工艺方法(编织方法)
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    public int insertConProductTechniqueType(ConProductTechniqueType conProductTechniqueType);

    /**
     * 修改生产工艺方法(编织方法)
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    public int updateConProductTechniqueType(ConProductTechniqueType conProductTechniqueType);

    /**
     * 变更生产工艺方法(编织方法)
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    public int changeConProductTechniqueType(ConProductTechniqueType conProductTechniqueType);

    /**
     * 批量删除生产工艺方法(编织方法)
     *
     * @param sids 需要删除的生产工艺方法(编织方法)ID
     * @return 结果
     */
    public int deleteConProductTechniqueTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conProductTechniqueType
    * @return
    */
    int changeStatus(ConProductTechniqueType conProductTechniqueType);

    /**
     * 更改确认状态
     * @param conProductTechniqueType
     * @return
     */
    int check(ConProductTechniqueType conProductTechniqueType);

    /**  获取下拉列表 */
    List<ConProductTechniqueType> getConProductTechniqueTypeList();
}
