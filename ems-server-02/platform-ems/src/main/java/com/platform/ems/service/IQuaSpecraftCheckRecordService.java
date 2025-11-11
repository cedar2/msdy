package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaSpecraftCheckRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 特殊工艺检测问题台账Service接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface IQuaSpecraftCheckRecordService extends IService<QuaSpecraftCheckRecord> {
    /**
     * 查询特殊工艺检测问题台账
     */
    public QuaSpecraftCheckRecord selectQuaSpecraftCheckRecordById(Long specraftCheckRecordSid);

    /**
     * 查询特殊工艺检测问题台账列表
     */
    public List<QuaSpecraftCheckRecord> selectQuaSpecraftCheckRecordList(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 新增特殊工艺检测问题台账
     */
    public int insertQuaSpecraftCheckRecord(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 修改特殊工艺检测问题台账
     */
    public int updateQuaSpecraftCheckRecord(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 变更特殊工艺检测问题台账
     */
    public int changeQuaSpecraftCheckRecord(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 批量删除特殊工艺检测问题台账
     */
    public int deleteQuaSpecraftCheckRecordByIds(List<Long> specraftCheckRecordSids);

    /**
     * 更改确认状态
     */
    int check(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
