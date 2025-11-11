package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinHuipiaoRecord;
import org.springframework.web.multipart.MultipartFile;

/**
 * 汇票台账表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinHuipiaoRecordService extends IService<FinHuipiaoRecord> {

    /**
     * 查询汇票台账表
     *
     * @param huipiaoRecordSid 汇票台账表ID
     * @return 汇票台账表
     */
    public FinHuipiaoRecord selectFinHuipiaoRecordById(Long huipiaoRecordSid);

    /**
     * 查询汇票台账表列表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 汇票台账表集合
     */
    public List<FinHuipiaoRecord> selectFinHuipiaoRecordList(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 新增汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int insertFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int updateFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 更改状态信息
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int setDateStatusByid(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 变更汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int changeFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 批量删除汇票台账表
     *
     * @param huipiaoRecordSids 需要删除的汇票台账表ID
     * @return 结果
     */
    public int deleteFinHuipiaoRecordByIds(List<Long> huipiaoRecordSids);

    /**
     * 更改确认状态
     *
     * @param finHuipiaoRecord 请求参数
     * @return
     */
    int check(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int updateRecordCompanyNew(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int updateRecordUseStatus(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    public int updateRecordUseRecord(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 导入
     * @param file
     * @return
     */
    Object importData(MultipartFile file);
}
