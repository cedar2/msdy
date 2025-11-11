package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecRecordFengyangAttach;

import java.util.List;

/**
 * 封样记录-附件Service接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface ITecRecordFengyangAttachService extends IService<TecRecordFengyangAttach> {
    /**
     * 查询封样记录-附件
     *
     * @param attachmentSid 封样记录-附件ID
     * @return 封样记录-附件
     */
    public TecRecordFengyangAttach selectTecRecordFengyangAttachById(Long attachmentSid);

    /**
     * 查询封样记录-附件列表
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 封样记录-附件集合
     */
    public List<TecRecordFengyangAttach> selectTecRecordFengyangAttachList(TecRecordFengyangAttach tecRecordFengyangAttach);

    /**
     * 新增封样记录-附件
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    public int insertTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach);

    /**
     * 修改封样记录-附件
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    public int updateTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach);

    /**
     * 变更封样记录-附件
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    public int changeTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach);

    /**
     * 批量删除封样记录-附件
     *
     * @param attachmentSids 需要删除的封样记录-附件ID
     * @return 结果
     */
    public int deleteTecRecordFengyangAttachByIds(List<Long> attachmentSids);

}
