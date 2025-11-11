package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaRawmatCheck;

import java.util.List;

/**
 * 面辅料检测单-主Service接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface IQuaRawmatCheckService extends IService<QuaRawmatCheck> {
    /**
     * 查询面辅料检测单-主
     *
     * @param rawmatCheckSid 面辅料检测单-主ID
     * @return 面辅料检测单-主
     */
    public QuaRawmatCheck selectQuaRawmatCheckById(Long rawmatCheckSid);

    /**
     * 查询面辅料检测单-主列表
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 面辅料检测单-主集合
     */
    public List<QuaRawmatCheck> selectQuaRawmatCheckList(QuaRawmatCheck quaRawmatCheck);

    /**
     * 新增面辅料检测单-主
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    public int insertQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck);

    /**
     * 修改面辅料检测单-主
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    public int updateQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck);

    /**
     * 变更面辅料检测单-主
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    public int changeQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck);

    /**
     * 批量删除面辅料检测单-主
     *
     * @param rawmatCheckSids 需要删除的面辅料检测单-主ID
     * @return 结果
     */
    public int deleteQuaRawmatCheckByIds(List<Long> rawmatCheckSids);

    /**
     * 更改确认状态
     *
     * @param quaRawmatCheck
     * @return
     */
    int check(QuaRawmatCheck quaRawmatCheck);

}
