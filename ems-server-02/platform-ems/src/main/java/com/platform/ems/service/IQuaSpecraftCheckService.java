package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaSpecraftCheck;

import java.util.List;

/**
 * 特殊工艺检测单-主Service接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface IQuaSpecraftCheckService extends IService<QuaSpecraftCheck> {
    /**
     * 查询特殊工艺检测单-主
     *
     * @param specraftCheckSid 特殊工艺检测单-主ID
     * @return 特殊工艺检测单-主
     */
    public QuaSpecraftCheck selectQuaSpecraftCheckById(Long specraftCheckSid);

    /**
     * 查询特殊工艺检测单-主列表
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 特殊工艺检测单-主集合
     */
    public List<QuaSpecraftCheck> selectQuaSpecraftCheckList(QuaSpecraftCheck quaSpecraftCheck);

    /**
     * 新增特殊工艺检测单-主
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    public int insertQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck);

    /**
     * 修改特殊工艺检测单-主
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    public int updateQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck);

    /**
     * 变更特殊工艺检测单-主
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    public int changeQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck);

    /**
     * 批量删除特殊工艺检测单-主
     *
     * @param specraftCheckSids 需要删除的特殊工艺检测单-主ID
     * @return 结果
     */
    public int deleteQuaSpecraftCheckByIds(List<Long> specraftCheckSids);

    /**
     * 更改确认状态
     *
     * @param quaSpecraftCheck
     * @return
     */
    int check(QuaSpecraftCheck quaSpecraftCheck);

}
