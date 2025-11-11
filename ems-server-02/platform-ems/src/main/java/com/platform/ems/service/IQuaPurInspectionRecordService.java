package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaPurInspectionRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 采购验货问题台账Service接口
 *
 * @author platform
 * @date 2024-09-20
 */
public interface IQuaPurInspectionRecordService extends IService<QuaPurInspectionRecord>{

    /**
     * 查询采购验货问题台账
     *
     * @param purInspectionRecordSid 采购验货问题台账ID
     * @return 采购验货问题台账
     */
    public QuaPurInspectionRecord selectQuaPurInspectionRecordById(Long purInspectionRecordSid);

    /**
     * 查询采购验货问题台账列表
     *
     * @param QuaPurInspectionRecord 采购验货问题台账
     * @return 采购验货问题台账集合
     */
    public List<QuaPurInspectionRecord> selectQuaPurInspectionRecordList(QuaPurInspectionRecord QuaPurInspectionRecord);

    /**
     * 新增采购验货问题台账
     *
     * @param QuaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    public int insertQuaPurInspectionRecord(QuaPurInspectionRecord QuaPurInspectionRecord);

    /**
     * 修改采购验货问题台账
     *
     * @param QuaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    public int updateQuaPurInspectionRecord(QuaPurInspectionRecord QuaPurInspectionRecord);

    /**
     * 变更采购验货问题台账
     *
     * @param QuaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    public int changeQuaPurInspectionRecord(QuaPurInspectionRecord QuaPurInspectionRecord);

    /**
     * 批量删除采购验货问题台账
     *
     * @param purInspectionRecordSids 需要删除的采购验货问题台账ID
     * @return 结果
     */
    public int deleteQuaPurInspectionRecordByIds(List<Long>  purInspectionRecordSids);


    /**
     * 更改确认状态
     * @param QuaPurInspectionRecord 请求参数
     * @return
     */
    int check(QuaPurInspectionRecord QuaPurInspectionRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
