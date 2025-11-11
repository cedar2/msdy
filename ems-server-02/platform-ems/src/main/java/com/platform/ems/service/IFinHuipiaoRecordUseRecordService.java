package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinHuipiaoRecord;
import com.platform.ems.domain.FinHuipiaoRecordUseRecord;

/**
 * 汇票台账-使用记录表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinHuipiaoRecordUseRecordService extends IService<FinHuipiaoRecordUseRecord>{

    /**
     * 查询汇票台账-使用记录表
     *
     * @param huipiaoRecordUseRecordSid 汇票台账-使用记录表ID
     * @return 汇票台账-使用记录表
     */
    public FinHuipiaoRecordUseRecord selectFinHuipiaoRecordUseRecordById(Long huipiaoRecordUseRecordSid);

    /**
     * 查询汇票台账-使用记录表列表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 汇票台账-使用记录表集合
     */
    public List<FinHuipiaoRecordUseRecord> selectFinHuipiaoRecordUseRecordList(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord);

    /**
     * 批量新增
     */
    int insertByList(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 批量修改
     */
    int updateByList(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 批量删除
     */
    public int deleteByList(List<FinHuipiaoRecordUseRecord> itemList);

    /**
     * 新增汇票台账-使用记录表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    public int insertFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord);

    /**
     * 修改汇票台账-使用记录表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    public int updateFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord);

    /**
     * 变更汇票台账-使用记录表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    public int changeFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord);

    /**
     * 批量删除汇票台账-使用记录表
     *
     * @param huipiaoRecordUseRecordSids 需要删除的汇票台账-使用记录表ID
     * @return 结果
     */
    public int deleteFinHuipiaoRecordUseRecordByIds(List<Long>  huipiaoRecordUseRecordSids);

}
