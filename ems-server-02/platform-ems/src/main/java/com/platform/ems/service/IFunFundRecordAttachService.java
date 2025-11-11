package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FunFundRecordAttach;

/**
 * 资金流水-附件Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IFunFundRecordAttachService extends IService<FunFundRecordAttach> {
    /**
     * 查询资金流水-附件
     *
     * @param fundRecordAttachSid 资金流水-附件ID
     * @return 资金流水-附件
     */
    public FunFundRecordAttach selectFunFundRecordAttachById(Long fundRecordAttachSid);

    /**
     * 查询资金流水-附件列表
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 资金流水-附件集合
     */
    public List<FunFundRecordAttach> selectFunFundRecordAttachList(FunFundRecordAttach funFundRecordAttach);

    /**
     * 新增资金流水-附件
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    public int insertFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach);

    /**
     * 修改资金流水-附件
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    public int updateFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach);

    /**
     * 变更资金流水-附件
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    public int changeFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach);

    /**
     * 批量删除资金流水-附件
     *
     * @param fundRecordAttachSids 需要删除的资金流水-附件ID
     * @return 结果
     */
    public int deleteFunFundRecordAttachByIds(List<Long> fundRecordAttachSids);

}
