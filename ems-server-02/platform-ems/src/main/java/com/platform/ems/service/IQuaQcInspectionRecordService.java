package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaQcInspectionRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * QC验货问题台账Service接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface IQuaQcInspectionRecordService extends IService<QuaQcInspectionRecord> {
    /**
     * 查询QC验货问题台账
     *
     * @param qcInspectionRecordSid QC验货问题台账ID
     * @return QC验货问题台账
     */
    public QuaQcInspectionRecord selectQuaQcInspectionRecordById(Long qcInspectionRecordSid);

    /**
     * 查询QC验货问题台账列表
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return QC验货问题台账集合
     */
    public List<QuaQcInspectionRecord> selectQuaQcInspectionRecordList(QuaQcInspectionRecord quaQcInspectionRecord);

    /**
     * 新增QC验货问题台账
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    public int insertQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord);

    /**
     * 修改QC验货问题台账
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    public int updateQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord);

    /**
     * 变更QC验货问题台账
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    public int changeQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord);

    /**
     * 批量删除QC验货问题台账
     *
     * @param qcInspectionRecordSids 需要删除的QC验货问题台账ID
     * @return 结果
     */
    public int deleteQuaQcInspectionRecordByIds(List<Long> qcInspectionRecordSids);

    /**
     * 更改确认状态
     *
     * @param quaQcInspectionRecord
     * @return
     */
    int check(QuaQcInspectionRecord quaQcInspectionRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
