package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaProductCheck;

import java.util.List;

/**
 * 成衣检测单-主Service接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface IQuaProductCheckService extends IService<QuaProductCheck> {
    /**
     * 查询成衣检测单-主
     *
     * @param productCheckSid 成衣检测单-主ID
     * @return 成衣检测单-主
     */
    public QuaProductCheck selectQuaProductCheckById(Long productCheckSid);

    /**
     * 查询成衣检测单-主列表
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 成衣检测单-主集合
     */
    public List<QuaProductCheck> selectQuaProductCheckList(QuaProductCheck quaProductCheck);

    /**
     * 新增成衣检测单-主
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    public int insertQuaProductCheck(QuaProductCheck quaProductCheck);

    /**
     * 修改成衣检测单-主
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    public int updateQuaProductCheck(QuaProductCheck quaProductCheck);

    /**
     * 变更成衣检测单-主
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    public int changeQuaProductCheck(QuaProductCheck quaProductCheck);

    /**
     * 批量删除成衣检测单-主
     *
     * @param productCheckSids 需要删除的成衣检测单-主ID
     * @return 结果
     */
    public int deleteQuaProductCheckByIds(List<Long> productCheckSids);

    /**
     * 更改确认状态
     *
     * @param quaProductCheck
     * @return
     */
    int check(QuaProductCheck quaProductCheck);

}
