package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecRecordTechtransfer;

import java.util.List;

/**
 * 技术转移记录Service接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface ITecRecordTechtransferService extends IService<TecRecordTechtransfer> {
    /**
     * 查询技术转移记录
     *
     * @param recordTechtransferSid 技术转移记录ID
     * @return 技术转移记录
     */
    public TecRecordTechtransfer selectTecRecordTechtransferById(Long recordTechtransferSid);

    /**
     * 查询技术转移记录列表
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 技术转移记录集合
     */
    public List<TecRecordTechtransfer> selectTecRecordTechtransferList(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 新增技术转移记录
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    public TecRecordTechtransfer insertTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 修改技术转移记录
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    public int updateTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 变更技术转移记录
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    public int changeTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 批量删除技术转移记录
     *
     * @param recordTechtransferSids 需要删除的技术转移记录ID
     * @return 结果
     */
    public int deleteTecRecordTechtransferByIds(List<Long> recordTechtransferSids);

    /**
     * 更改确认状态
     *
     * @param tecRecordTechtransfer
     * @return
     */
    int check(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 单据提交
     */
    int verify(TecRecordTechtransfer tecRecordTechtransfer);
}
