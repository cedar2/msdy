package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecModelLinePos;

import java.util.List;

/**
 * 版型-线部位Service接口
 *
 * @author linhongwei
 * @date 2021-10-19
 */
public interface ITecModelLinePosService extends IService<TecModelLinePos> {
    /**
     * 查询版型-线部位
     *
     * @param modelLinePosSid 版型-线部位ID
     * @return 版型-线部位
     */
    public TecModelLinePos selectTecModelLinePosById(Long modelLinePosSid);

    /**
     * 查询版型-线部位列表
     *
     * @param tecModelLinePos 版型-线部位
     * @return 版型-线部位集合
     */
    public List<TecModelLinePos> selectTecModelLinePosList(TecModelLinePos tecModelLinePos);

    /**
     * 新增版型-线部位
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    public int insertTecModelLinePos(TecModelLinePos tecModelLinePos);

    /**
     * 修改版型-线部位
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    public int updateTecModelLinePos(TecModelLinePos tecModelLinePos);

    /**
     * 变更版型-线部位
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    public int changeTecModelLinePos(TecModelLinePos tecModelLinePos);

    /**
     * 批量删除版型-线部位
     *
     * @param modelLinePosSids 需要删除的版型-线部位ID
     * @return 结果
     */
    public int deleteTecModelLinePosByIds(List<Long> modelLinePosSids);

}
