package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConProduceStage;

import java.util.List;

/**
 * 所属生产阶段Service接口
 *
 * @author linhongwei
 * @date 2021-09-26
 */
public interface IConProduceStageService extends IService<ConProduceStage> {
    /**
     * 查询所属生产阶段
     *
     * @param sid 所属生产阶段ID
     * @return 所属生产阶段
     */
    public ConProduceStage selectConProduceStageById(Long sid);

    /**
     * 查询所属生产阶段列表
     *
     * @param conProduceStage 所属生产阶段
     * @return 所属生产阶段集合
     */
    public List<ConProduceStage> selectConProduceStageList(ConProduceStage conProduceStage);

    /**
     * 新增所属生产阶段
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    public int insertConProduceStage(ConProduceStage conProduceStage);

    /**
     * 修改所属生产阶段
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    public int updateConProduceStage(ConProduceStage conProduceStage);

    /**
     * 变更所属生产阶段
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    public int changeConProduceStage(ConProduceStage conProduceStage);

    /**
     * 批量删除所属生产阶段
     *
     * @param sids 需要删除的所属生产阶段ID
     * @return 结果
     */
    public int deleteConProduceStageByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conProduceStage
     * @return
     */
    int changeStatus(ConProduceStage conProduceStage);

    /**
     * 更改确认状态
     *
     * @param conProduceStage
     * @return
     */
    int check(ConProduceStage conProduceStage);

    /**
     * 所属生产阶段下拉框列表
     */
    List<ConProduceStage> getList(ConProduceStage conProduceStage);
}
