package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinHuipiaoRecordAttach;

import java.util.List;

/**
 * 汇票台账-附件Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IFinHuipiaoRecordAttachService extends IService<FinHuipiaoRecordAttach> {
    /**
     * 查询汇票台账-附件
     *
     * @param huipiaoRecordAttachSid 汇票台账-附件ID
     * @return 汇票台账-附件
     */
    public FinHuipiaoRecordAttach selectFinHuipiaoRecordAttachById(Long huipiaoRecordAttachSid);

    /**
     * 查询汇票台账-附件列表
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 汇票台账-附件集合
     */
    public List<FinHuipiaoRecordAttach> selectFinHuipiaoRecordAttachList(FinHuipiaoRecordAttach finHuipiaoRecordAttach);

    /**
     * 新增汇票台账-附件
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    public int insertFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach);

    /**
     * 修改汇票台账-附件
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    public int updateFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach);

    /**
     * 变更汇票台账-附件
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    public int changeFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach);

    /**
     * 批量删除汇票台账-附件
     *
     * @param huipiaoRecordAttachSids 需要删除的汇票台账-附件ID
     * @return 结果
     */
    public int deleteFinHuipiaoRecordAttachByIds(List<Long> huipiaoRecordAttachSids);

}
