package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaShouhouRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 售后质量问题台账Service接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface IQuaShouhouRecordService extends IService<QuaShouhouRecord> {
    /**
     * 查询售后质量问题台账
     *
     * @param shouhouRecordSid 售后质量问题台账ID
     * @return 售后质量问题台账
     */
    public QuaShouhouRecord selectQuaShouhouRecordById(Long shouhouRecordSid);

    /**
     * 查询售后质量问题台账列表
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 售后质量问题台账集合
     */
    public List<QuaShouhouRecord> selectQuaShouhouRecordList(QuaShouhouRecord quaShouhouRecord);

    /**
     * 新增售后质量问题台账
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    public int insertQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord);

    /**
     * 修改售后质量问题台账
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    public int updateQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord);

    /**
     * 变更售后质量问题台账
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    public int changeQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord);

    /**
     * 批量删除售后质量问题台账
     *
     * @param shouhouRecordSids 需要删除的售后质量问题台账ID
     * @return 结果
     */
    public int deleteQuaShouhouRecordByIds(List<Long> shouhouRecordSids);

    /**
     * 更改确认状态
     *
     * @param quaShouhouRecord
     * @return
     */
    int check(QuaShouhouRecord quaShouhouRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
