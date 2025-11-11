package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecModelLine;
import com.platform.ems.domain.TecModelLinePos;

import java.util.List;

/**
 * 版型线Service接口
 *
 * @author linhongwei
 * @date 2021-10-19
 */
public interface ITecModelLineService extends IService<TecModelLine> {
    /**
     * 查询版型线
     *
     * @param modelSid 版型ID
     * @return 版型线
     */
    public TecModelLine selectTecModelLineById(Long modelSid);

    /**
     * 查询版型线列表
     *
     * @param tecModelLine 版型线
     * @return 版型线集合
     */
    public List<TecModelLine> selectTecModelLineList(TecModelLine tecModelLine);

    /**
     * 新增版型线
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    public int insertTecModelLine(TecModelLine tecModelLine);

    /**
     * 修改版型线
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    public int updateTecModelLine(TecModelLine tecModelLine);

    /**
     * 变更版型线
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    public int changeTecModelLine(TecModelLine tecModelLine);

    /**
     * 批量删除版型线
     *
     * @param modelLineSids 需要删除的版型线ID
     * @return 结果
     */
    public int deleteTecModelLineByIds(List<Long> modelLineSids);

    /**
     * 更改确认状态
     *
     * @param tecModelLine
     * @return
     */
    int check(TecModelLine tecModelLine);

    /**
     * 添加线部位时校验名称是否重复
     */
    TecModelLinePos verifyPosition(TecModelLinePos tecModelLinePos);
}
