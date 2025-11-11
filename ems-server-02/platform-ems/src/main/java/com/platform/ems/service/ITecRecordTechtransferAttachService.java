package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecRecordTechtransferAttach;

import java.util.List;

/**
 * 技术转移记录-附件Service接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface ITecRecordTechtransferAttachService extends IService<TecRecordTechtransferAttach> {
    /**
     * 查询技术转移记录-附件
     *
     * @param attachmentSid 技术转移记录-附件ID
     * @return 技术转移记录-附件
     */
    public TecRecordTechtransferAttach selectTecRecordTechtransferAttachById(Long attachmentSid);

    /**
     * 查询技术转移记录-附件列表
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 技术转移记录-附件集合
     */
    public List<TecRecordTechtransferAttach> selectTecRecordTechtransferAttachList(TecRecordTechtransferAttach tecRecordTechtransferAttach);

    /**
     * 新增技术转移记录-附件
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    public int insertTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach);

    /**
     * 修改技术转移记录-附件
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    public int updateTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach);

    /**
     * 变更技术转移记录-附件
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    public int changeTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach);

    /**
     * 批量删除技术转移记录-附件
     *
     * @param attachmentSids 需要删除的技术转移记录-附件ID
     * @return 结果
     */
    public int deleteTecRecordTechtransferAttachByIds(List<Long> attachmentSids);

}
