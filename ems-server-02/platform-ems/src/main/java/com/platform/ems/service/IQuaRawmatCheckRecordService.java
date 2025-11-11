package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaRawmatCheckRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物料检测问题台账Service接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface IQuaRawmatCheckRecordService extends IService<QuaRawmatCheckRecord> {
    /**
     * 查询物料检测问题台账
     */
    public QuaRawmatCheckRecord selectQuaRawmatCheckRecordById(Long rawmatCheckRecordSid);

    /**
     * 查询物料检测问题台账列表
     */
    public List<QuaRawmatCheckRecord> selectQuaRawmatCheckRecordList(QuaRawmatCheckRecord quaRawmatCheckRecord);

    /**
     * 新增物料检测问题台账
     */
    public int insertQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord);

    /**
     * 修改物料检测问题台账
     */
    public int updateQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord);

    /**
     * 变更物料检测问题台账
     */
    public int changeQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord);

    /**
     * 批量删除物料检测问题台账
     */
    public int deleteQuaRawmatCheckRecordByIds(List<Long> rawmatCheckRecordSids);

    /**
     * 更改确认状态
     */
    int check(QuaRawmatCheckRecord quaRawmatCheckRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
