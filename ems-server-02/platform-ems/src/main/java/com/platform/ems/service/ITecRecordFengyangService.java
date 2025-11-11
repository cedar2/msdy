package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecRecordFengyang;

import java.util.List;

/**
 * 封样记录(标准封样、产前封样)Service接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface ITecRecordFengyangService extends IService<TecRecordFengyang> {
    /**
     * 查询封样记录(标准封样、产前封样)
     *
     * @param recordFengyangSid 封样记录(标准封样、产前封样)ID
     * @return 封样记录(标准封样 、 产前封样)
     */
    public TecRecordFengyang selectTecRecordFengyangById(Long recordFengyangSid);

    /**
     * 查询封样记录(标准封样、产前封样)列表
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 封样记录(标准封样 、 产前封样)集合
     */
    public List<TecRecordFengyang> selectTecRecordFengyangList(TecRecordFengyang tecRecordFengyang);

    /**
     * 新增封样记录(标准封样、产前封样)
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    public TecRecordFengyang insertTecRecordFengyang(TecRecordFengyang tecRecordFengyang);

    /**
     * 修改封样记录(标准封样、产前封样)
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    public int updateTecRecordFengyang(TecRecordFengyang tecRecordFengyang);

    /**
     * 变更封样记录(标准封样、产前封样)
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    public int changeTecRecordFengyang(TecRecordFengyang tecRecordFengyang);

    /**
     * 批量删除封样记录(标准封样、产前封样)
     *
     * @param recordFengyangSids 需要删除的封样记录(标准封样、产前封样)ID
     * @return 结果
     */
    public int deleteTecRecordFengyangByIds(List<Long> recordFengyangSids);

    /**
     * 更改确认状态
     *
     * @param tecRecordFengyang
     * @return
     */
    int check(TecRecordFengyang tecRecordFengyang);

    /**
     * 单据提交校验
     */
    int verify(TecRecordFengyang tecRecordFengyang);
}
