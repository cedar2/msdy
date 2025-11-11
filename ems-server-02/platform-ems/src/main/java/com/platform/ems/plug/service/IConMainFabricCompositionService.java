package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMainFabricComposition;

/**
 * 主面料成分Service接口
 *
 * @author chenkw
 * @date 2022-06-01
 */
public interface IConMainFabricCompositionService extends IService<ConMainFabricComposition> {
    /**
     * 查询主面料成分
     *
     * @param sid 主面料成分ID
     * @return 主面料成分
     */
    public ConMainFabricComposition selectConMainFabricCompositionById(Long sid);

    /**
     * 查询主面料成分列表
     *
     * @param conMainFabricComposition 主面料成分
     * @return 主面料成分集合
     */
    public List<ConMainFabricComposition> selectConMainFabricCompositionList(ConMainFabricComposition conMainFabricComposition);

    /**
     * 新增主面料成分
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    public int insertConMainFabricComposition(ConMainFabricComposition conMainFabricComposition);

    /**
     * 修改主面料成分
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    public int updateConMainFabricComposition(ConMainFabricComposition conMainFabricComposition);

    /**
     * 变更主面料成分
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    public int changeConMainFabricComposition(ConMainFabricComposition conMainFabricComposition);

    /**
     * 批量删除主面料成分
     *
     * @param sids 需要删除的主面料成分ID
     * @return 结果
     */
    public int deleteConMainFabricCompositionByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conMainFabricComposition
     * @return
     */
    int changeStatus(ConMainFabricComposition conMainFabricComposition);

    /**
     * 更改确认状态
     *
     * @param conMainFabricComposition
     * @return
     */
    int check(ConMainFabricComposition conMainFabricComposition);

}
