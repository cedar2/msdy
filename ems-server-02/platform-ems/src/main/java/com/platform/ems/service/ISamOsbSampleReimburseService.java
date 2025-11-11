package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.SamOsbSampleReimburse;
import com.platform.ems.domain.dto.request.SamOsbSampleReimburseReportRequert;

import java.util.List;

/**
 * 外采样报销单-主Service接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface ISamOsbSampleReimburseService extends IService<SamOsbSampleReimburse>{
    /**
     * 查询外采样报销单-主
     *
     * @param reimburseSid 外采样报销单-主ID
     * @return 外采样报销单-主
     */
    public SamOsbSampleReimburse selectSamOsbSampleReimburseById (Long reimburseSid);

    /**
     * 查询外采样报销单-主列表
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 外采样报销单-主集合
     */
    public List<SamOsbSampleReimburse> selectSamOsbSampleReimburseList (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 新增外采样报销单-主
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    public int insertSamOsbSampleReimburse (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 修改外采样报销单-主
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    public int updateSamOsbSampleReimburse (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 变更外采样报销单-主
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    public int changeSamOsbSampleReimburse (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 批量删除外采样报销单-主
     *
     * @param reimburseSids 需要删除的外采样报销单-主ID
     * @return 结果
     */
    public int deleteSamOsbSampleReimburseByIds (List<Long> reimburseSids);

    /**
     * 更改确认状态
     * @param samOsbSampleReimburse
     * @return
     */
    int check (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 外采样报销明细报表查询
     * @param requert
     * @return
     */
    List<SamOsbSampleReimburseReportRequert> selectReport(SamOsbSampleReimburseReportRequert requert);

    public int submitItem(Long formSid);

    public int over(Long formSid);

    public int returned(Long formSid);

    public boolean itemValidation(Long sid);

    public SamOsbSampleReimburse isCreate(List<Long> materialSidList);

    public String wbx(Long sid);
}
